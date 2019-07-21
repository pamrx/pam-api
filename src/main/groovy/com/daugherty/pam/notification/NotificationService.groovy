package com.daugherty.pam.notification

import com.daugherty.pam.exception.ERROR_CODE
import com.daugherty.pam.patient.PatientMetadata
import com.daugherty.pam.patient.PatientPrescription
import com.notnoop.apns.APNS
import com.notnoop.apns.ApnsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.core.io.ClassPathResource
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

import java.time.Instant

@Service
class NotificationService {
  private final PatientNotificationRepository patientNotificationRepository

  @Autowired
  Environment env

  NotificationService(final PatientNotificationRepository patientNotificationRepository) {
    this.patientNotificationRepository = patientNotificationRepository
  }

  void sendNotification(PatientMetadata patientMetadata, PatientPrescription patientPrescription) {
    def medicationName = patientPrescription.drug.split(" ").first()
    def notification = new PatientNotification(
        patientId: patientMetadata.patientId,
        prescriptionId: patientPrescription.prescriptionId,
        initialNotificationTime: Instant.now()
    )
    patientNotificationRepository.save(notification)
    push(patientMetadata, "PAM Reminder", "Have you taken your ${medicationName} today?")
  }

  private void push(PatientMetadata patientMetadata, String title, String message) {
    try {
      InputStream inputStream = new ClassPathResource("pushcert.p12").getInputStream()
      ApnsService service = APNS.newService().withCert(inputStream, env.getProperty('pam.notification.password'))
          .withSandboxDestination().build()
      String payload = APNS.newPayload().alertTitle(title).alertBody(message).category('confirm').build()
      service.push(patientMetadata.notificationToken, payload)
    } catch (IOException e) {
      e.printStackTrace()
    }
  }

  void updateNotificationResponse(String notificationId, RESPONSE response) {
    def optionalNotification = patientNotificationRepository.findById(notificationId)
    if (optionalNotification.isPresent()) {
      def notification
    } else {
      throw PamException(ERROR_CODE.NOT_FOUND)
    }
  }

  @Scheduled(fixedRateString = '60000')
  void evaluateNotifications() {

  }
}
