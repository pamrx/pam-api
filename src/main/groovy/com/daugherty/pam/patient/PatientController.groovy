package com.daugherty.pam.patient

import com.daugherty.pam.exception.PamException
import com.daugherty.pam.notification.NotificationService
import groovy.util.logging.Slf4j
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Slf4j
@RestController
class PatientController {
  private final PatientService patientService
  private final NotificationService notificationService

  PatientController(final PatientService patientService, final NotificationService notificationService) {
    this.patientService = patientService
    this.notificationService = notificationService
  }

  @GetMapping('/patients')
  ResponseEntity<List<Patient>> getPatients() {
    try {
      ResponseEntity.ok(patientService.getPatients())
    } catch (PamException e) {
      ResponseEntity.badRequest().build()
    }
  }

  @GetMapping('/patients/{patientId}')
  ResponseEntity<Patient> getPatientById(@PathVariable String patientId) {
    try {
      ResponseEntity.ok(patientService.getPatientById(patientId))
    } catch (PamException e) {
      ResponseEntity.notFound().build()
    }
  }

  @GetMapping('/patients/{username}')
  ResponseEntity<Patient> getPatientByUsername(@PathVariable String username) {
    try {
      ResponseEntity.ok(patientService.getPatientByUsername(username))
    } catch (PamException e) {
      ResponseEntity.notFound().build()
    }
  }

  @PostMapping('/patients/{patientId}/prescriptions')
  ResponseEntity addPrescriptionToPatient(@PathVariable String patientId, @RequestBody PatientPrescription prescription) {
    patientService.addPrescriptiontoPatient(patientId, prescription)
  }

  @PostMapping('/patients/{patientId}/notificationToken')
  ResponseEntity addNotificationTokenToPatient(@PathVariable String patientId, @RequestBody String notificationToken) {
    patientService.addNotificationTokenToPatient(patientId, notificationToken)
  }
}
