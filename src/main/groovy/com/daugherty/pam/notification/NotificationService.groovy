package com.daugherty.pam.notification

import com.daugherty.pam.emr.EmrService
import com.daugherty.pam.exception.ERROR_CODE
import com.daugherty.pam.patient.*
import com.notnoop.apns.APNS
import com.notnoop.apns.ApnsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.core.io.ClassPathResource
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

import java.time.Instant
import java.time.temporal.ChronoUnit

@Service
class NotificationService {
  private final EmrService emrService
  private final PatientNotificationRepository patientNotificationRepository
  private final PatientPrescriptionRepository patientPrescriptionRepository
  private final PatientMetadataRepository patientMetadataRepository

  private final int SNOOZE_SECONDS = 300

  @Autowired
  Environment env

  NotificationService(final EmrService emrService, final PatientNotificationRepository patientNotificationRepository,
                      final PatientPrescriptionRepository patientPrescriptionRepository,
                      final PatientMetadataRepository patientMetadataRepository) {
    this.emrService = emrService
    this.patientNotificationRepository = patientNotificationRepository
    this.patientPrescriptionRepository = patientPrescriptionRepository
    this.patientMetadataRepository = patientMetadataRepository
  }

  PatientNotification findLatestNotification(String patientId, String prescriptionId) {
    patientNotificationRepository.findTopByPatientIdAndPrescriptionId(patientId, prescriptionId)
  }

  PatientNotification sendNotification(PatientMetadata patientMetadata, PatientPrescription patientPrescription) {
    def medicationName = patientPrescription.drug.split(" ").first()
    def notification = new PatientNotification(
      patientId: patientMetadata.patientId,
      prescriptionId: patientPrescription.id,
      initialNotificationTime: Instant.now()
    )
    def savedNotification = patientNotificationRepository.save(notification)
    push(patientMetadata, savedNotification.id, "PAM Reminder", "Have you taken your ${medicationName} today?")
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
    def optionalNotification = patientNotificationRepository.findById(notificationId)
    if (optionalNotification.isPresent()) {
      def notification = optionalNotification.get()
      notification.response = response
      notification.responseTime = Instant.now()
      return patientNotificationRepository.save(notification)
    } else {
      throw PamException(ERROR_CODE.NOT_FOUND)
    }
  }

  @Scheduled(fixedRateString = '60000')
  void evaluateNotifications() {
    patientNotificationRepository.findAll()
      .findAll { it.response = RESPONSE.SNOOZE }
      .each {
        if (it.responseTime?.plusSeconds(SNOOZE_SECONDS)?.isAfter(Instant.now())) {
          sendNotification(
            patientMetadataRepository.findByPatientId(it.patientId),
            patientPrescriptionRepository.findTopById(it.prescriptionId)
          )
        }
      }

    patientPrescriptionRepository
      .findAll()
      .each {
        def latestNotification = findLatestNotification(it.patient_id, it.id)
        switch (it.interval) {
          case PRESCRIPTION_INTERVAL.DAILY:
            if (latestNotification.initialNotificationTime.isBefore(Instant.now().minus(1, ChronoUnit.DAYS))) {
              sendNotification(patientMetadataRepository.findByPatientId(it.patient_id), it)
            }
            break
          case PRESCRIPTION_INTERVAL.TWICE_DAILY:
            if (latestNotification.initialNotificationTime.isBefore(Instant.now().minus(12, ChronoUnit.HOURS))) {
              sendNotification(patientMetadataRepository.findByPatientId(it.patient_id), it)
            }
            break
          case PRESCRIPTION_INTERVAL.AS_NEEDED:
            // At  this time we have decided not to notify for this interval
            break
        }
      }
  }
}
