package com.daugherty.pam.patient

import com.daugherty.pam.exception.PamException
import com.daugherty.pam.notification.NotificationService
import groovy.util.logging.Slf4j
import org.springframework.http.HttpStatus
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

  @GetMapping('/patients/{username}/login')
  ResponseEntity<Patient> getPatientByUsername(@PathVariable String username) {
    try {
      ResponseEntity.ok(patientService.getPatientByUsername(username))
    } catch (PamException e) {
      ResponseEntity.notFound().build()
    }
  }

  @PutMapping('/patients/{patientId}')
  ResponseEntity<Patient> updatePatient(@PathVariable String patientId, @RequestBody Patient patient) {
    try {
      ResponseEntity.ok(patientService.updatePatient(patient))
    } catch (PamException e) {
      ResponseEntity.notFound().build()
    }
  }

  @PostMapping('/patients/{patientId}/prescriptions')
  ResponseEntity addPrescriptionToPatient(@PathVariable String patientId, @RequestBody PatientPrescription prescription) {
    patientService.addPrescriptionToPatient(patientId, prescription)
    ResponseEntity.status(HttpStatus.CREATED).build()
  }

  @GetMapping('/patients/{patientId}/prescriptions')
  ResponseEntity<List<PatientPrescription>> getPrescriptionsForPatient(@PathVariable String patientId) {
    ResponseEntity.ok(patientService.getPatientPrescriptions(patientId))
  }

  @PostMapping('/patients/{patientId}/notificationToken')
  ResponseEntity addNotificationTokenToPatient(@PathVariable String patientId, @RequestBody String notificationToken) {
    patientService.addNotificationTokenToPatient(patientId, notificationToken)
    ResponseEntity.status(HttpStatus.CREATED).build()
  }
}
