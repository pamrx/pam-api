package com.daugherty.pam.notification

import com.daugherty.pam.emr.EmrService
import com.daugherty.pam.exception.ERROR_CODE
import com.daugherty.pam.exception.PamException
import com.daugherty.pam.patient.*
import com.notnoop.apns.APNS
import com.notnoop.apns.ApnsService
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.core.io.ClassPathResource
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

import java.time.Instant
import java.time.temporal.ChronoUnit

@CompileStatic
@Slf4j
@Service
class NotificationService {
  private final EmrService emrService
  private final PatientNotificationRepository patientNotificationRepository
  private final PatientPrescriptionRepository patientPrescriptionRepository
  private final PatientMetadataRepository patientMetadataRepository
  private final PatientService patientService

  private final int SNOOZE_SECONDS = 300

  @Autowired
  Environment env

  NotificationService(final EmrService emrService, final PatientNotificationRepository patientNotificationRepository,
                      final PatientPrescriptionRepository patientPrescriptionRepository,
                      final PatientMetadataRepository patientMetadataRepository, final PatientService patientService) {
    this.emrService = emrService
    this.patientNotificationRepository = patientNotificationRepository
    this.patientPrescriptionRepository = patientPrescriptionRepository
    this.patientMetadataRepository = patientMetadataRepository
    this.patientService = patientService
  }

  PatientNotification findLatestNotification(String patientId, String prescriptionId) {
    patientNotificationRepository.findTopByPatientIdAndPrescriptionIdOrderByLastNotificationTime(patientId, prescriptionId)
  }

  PatientNotification resendNotification(PatientMetadata patientMetadata, PatientPrescription patientPrescription, PatientNotification previousNotification) {
    def medicationName = patientPrescription.drug.split(" ").first()
    previousNotification.lastNotificationTime = Instant.now()
    push(patientMetadata, previousNotification.id, "PAM Reminder", "Have you taken your ${medicationName} today?")
    patientNotificationRepository.save(previousNotification)
  }

  PatientNotification sendNewNotification(PatientMetadata patientMetadata, PatientPrescription patientPrescription, String message) {
    def notification = new PatientNotification(
      patientId: patientMetadata.patientId,
      prescriptionId: patientPrescription.id,
      initialNotificationTime: Instant.now(),
      lastNotificationTime: Instant.now()
    )
    def savedNotification = patientNotificationRepository.insert(notification)
    push(patientMetadata, savedNotification.id, "PAM Reminder", message)
    savedNotification
  }

  private void push(PatientMetadata patientMetadata, String notificationId, String title, String message) {
    try {
      InputStream inputStream = new ClassPathResource("pushcert.p12").getInputStream()
      ApnsService service = APNS.newService().withCert(inputStream, env.getProperty('pam.notification.password'))
        .withSandboxDestination().build()
      String payload = APNS.newPayload().alertTitle(title).alertBody(message).category('confirm')
        .customField('notificationId', notificationId).build()
      service.push(patientMetadata.notificationToken, payload)
    } catch (IOException e) {
      e.printStackTrace()
    }
  }

  PatientNotification updateNotificationResponse(String notificationId, RESPONSE response) {
    def notification = patientNotificationRepository.findById(notificationId).orElse(null)
    if (notification) {
      notification.response = response
      notification.responseTime = Instant.now()
      return patientNotificationRepository.save(notification)
    } else {
      throw new PamException(ERROR_CODE.NOT_FOUND)
    }
  }

  @Scheduled(fixedRateString = '60000')
  void evaluateNotifications() {
    log.info('Evaluating snoozed notifications')
    def notifications = patientNotificationRepository.findAll()

    notifications
      .findAll { it.response == RESPONSE.SNOOZE }
      .each { notification ->
        if (Instant.now().isAfter(notification.responseTime?.plusSeconds(SNOOZE_SECONDS))) {
          resendNotification(
            patientMetadataRepository.findByPatientId(notification.patientId),
            patientPrescriptionRepository.findTopById(notification.prescriptionId),
            notification
          )
        }
      }

    def notificationsByPatientId = notifications
      .groupBy { notification -> notification.patientId }

    notificationsByPatientId.keySet().each { patientId ->
      log.info('Calculating Score for Patient ' + patientId)
      def patientScore = BigDecimal.ZERO
        notificationsByPatientId[patientId]
        .toSorted { a, b -> a.responseTime <=> b.responseTime }
        .withIndex()
        .each { notificationAndIndex ->
          def weight = (notificationsByPatientId[patientId].size() - (notificationAndIndex.second + 1)) / notificationsByPatientId[patientId].size()
          switch (notificationAndIndex.first.response) {
            case RESPONSE.YES:
              patientScore = patientScore + weight * BigDecimal.valueOf(10)
              break
            case RESPONSE.IGNORE:
              patientScore = patientScore + weight * BigDecimal.valueOf(-10)
              break
          }
        }
      patientScore = patientScore * 1000 // We aren't certain why this conversion factor is required, but it seems to be
      patientService.updatePatientPrescriptionAdherenceScore(patientId, normalizeScore(patientScore.floatValue(), 0f, 100f).toInteger())
    }

    log.info('Evaluating new notifications')
    patientPrescriptionRepository
      .findAll()
      .each { prescription ->
        def patientMetadata = patientMetadataRepository.findByPatientId(prescription.patient_id)
        if (patientMetadata?.notificationToken) { //only evaluate patients with logins
          def latestNotification = findLatestNotification(prescription.patient_id, prescription.id)
          def medicationName = prescription.drug.split(" ").first()
          if (!latestNotification) {
            switch (prescription.interval) {
              case PRESCRIPTION_INTERVAL.DAILY:
                sendNewNotification(patientMetadata, prescription, "Have you taken your ${medicationName} today?")
                break
              case PRESCRIPTION_INTERVAL.TWICE_DAILY:
                sendNewNotification(patientMetadata, prescription, "Have you taken your first ${medicationName} today?")
                break
              case PRESCRIPTION_INTERVAL.AS_NEEDED:
                // At  this time we have decided not to notify for this interval
                break
            }
          } else {
            switch (prescription.interval) {
              case PRESCRIPTION_INTERVAL.DAILY:
                if (latestNotification.lastNotificationTime.isBefore(Instant.now().minus(1, ChronoUnit.DAYS))) {
                  sendNewNotification(patientMetadata, prescription, "Have you taken your ${medicationName} today?")
                }
                break
              case PRESCRIPTION_INTERVAL.TWICE_DAILY:
                if (latestNotification.lastNotificationTime.isBefore(Instant.now().minus(12, ChronoUnit.HOURS))) {
                  sendNewNotification(patientMetadata, prescription, "Have you taken your next ${medicationName}?")
                }
                break
              case PRESCRIPTION_INTERVAL.AS_NEEDED:
                // At  this time we have decided not to notify for this interval
                break
            }
          }
        }
      }
  }

  private static Float normalizeScore(Float score, Float min, Float max) {
    def a = ((score - min) / (max - min))
    return a.toFloat()
  }
}

