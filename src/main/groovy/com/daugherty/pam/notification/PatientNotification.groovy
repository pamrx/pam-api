package com.daugherty.pam.notification

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

import java.time.Instant

@Document(collection = 'patient_notifications')
class PatientNotification {
  @Id
  String id
  String patientId
  String prescriptionId
  Instant initialNotificationTime
  Instant lastNotificationTime
  RESPONSE response
  Instant responseTime
}

