package com.daugherty.pam.patient

import com.daugherty.pam.exception.PamException
import com.daugherty.pam.notification.NotificationService
import groovy.util.logging.Slf4j
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@Slf4j
@RestController()
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
}
