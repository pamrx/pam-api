package com.daugherty.pam.notification

import com.daugherty.pam.medication.MedicationService
import com.daugherty.pam.patient.PatientService
import groovy.util.logging.Slf4j
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@Slf4j
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

  @PostMapping('/notify/{patientId}/{medicationId}')
  ResponseEntity sendNotification(@PathVariable String patientId, @PathVariable String medicationId) {
    def patient = patientService.getPatientById(patientId)
    def medication = medicationService.getMedicationById(medicationId)
    notificationService.show(patient, "Pam Reminder", "Have you taken your ${medication.title} today?")
  }

  @PostMapping('/{patientId}/{medicationId}/yes')
  ResponseEntity yesResponse(@PathVariable String patientId, @PathVariable String medicationId) {
    patientService.incrementPostiveResponse(patientId, medicationId)
  }

  @PostMapping('/{patientId}/{medicationId}/no')
  ResponseEntity noResponse(@PathVariable String patientId, @PathVariable String medicationId) {
    patientService.decrementPostiveResponse(patientId, medicationId)
  }
}
