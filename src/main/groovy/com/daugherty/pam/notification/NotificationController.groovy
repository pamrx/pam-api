package com.daugherty.pam.notification

import com.daugherty.pam.medication.MedicationService
import com.daugherty.pam.patient.PatientService
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@Slf4j
@CompileStatic
@RestController()
class NotificationController {
  private final NotificationService notificationService
  private final MedicationService medicationService
  private final PatientService patientService

  NotificationController(final NotificationService notificationService, final MedicationService medicationService, final PatientService patientService) {
    this.notificationService = notificationService
    this.medicationService = medicationService
    this.patientService = patientService
  }

  @PostMapping('/notify/{patientId}/{prescriptionId}')
  ResponseEntity<PatientNotification> sendNotification(@PathVariable String patientId, @PathVariable String prescriptionId) {
    def patientMetadata = patientService.getPatientMetadataByPatientId(patientId)
    def patientPrescription = patientService.getPatientPrescriptionByPrescriptionId(prescriptionId)
    def notification = notificationService.sendNewNotification(patientMetadata, patientPrescription)
    ResponseEntity.ok(notification)
  }

  @PostMapping('/notifications/{notificationId}')
  ResponseEntity<PatientNotification> yesResponse(@PathVariable String notificationId, @RequestBody RESPONSE response) {
    def notification = notificationService.updateNotificationResponse(notificationId, response)
    ResponseEntity.ok(notification)
  }
}
