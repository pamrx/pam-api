package com.daugherty.pam.notification

import com.daugherty.pam.patient.Patient
import com.notnoop.apns.APNS
import com.notnoop.apns.ApnsService
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Service

@Service
class NotificationService {

  void show(Patient patient, String title, String message) {
    try {
      InputStream inputStream = new ClassPathResource("pushcert.p12").getInputStream()
      ApnsService service = APNS.newService().withCert(inputStream, "").withSandboxDestination().build()
      String payload = APNS.newPayload().alertTitle(title).alertBody(message).build()
      service.push(patient.notificationToken, payload)
    } catch (IOException e) {
      e.printStackTrace()
    }
  }
}
