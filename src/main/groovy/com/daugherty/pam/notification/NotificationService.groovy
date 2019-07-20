package com.daugherty.pam.notification

import com.daugherty.pam.patient.Patient
import com.notnoop.apns.APNS
import com.notnoop.apns.ApnsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.core.io.ClassPathResource
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class NotificationService {
  private final PatientNotificationRepository patientNotificationRepository

  @Autowired
  Environment env

  NotificationService(final PatientNotificationRepository patientNotificationRepository) {
    this.patientNotificationRepository = patientNotificationRepository
  }

  void show(Patient patient, String title, String message) {
    try {
      InputStream inputStream = new ClassPathResource("pushcert.p12").getInputStream()
      ApnsService service = APNS.newService().withCert(inputStream, env.getProperty('pam.notification.password'))
          .withSandboxDestination().build()
      String payload = APNS.newPayload().alertTitle(title).alertBody(message).category('confirm').build()
      service.push(patient.notificationToken, payload)
    } catch (IOException e) {
      e.printStackTrace()
    }
  }

  @Scheduled(fixedRateString = '60000')
  void evaluateNotifications() {

  }
}
