package com.daugherty.pam.patient

import com.daugherty.pam.exception.PamException
import com.daugherty.pam.notification.NotificationService
import groovy.util.logging.Slf4j
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@Slf4j
@RestController('/patients')
class PatientController {
  private final PatientService patientService
  private final NotificationService notificationService

  PatientController(final PatientService patientService, final NotificationService notificationService) {
    this.patientService = patientService
    this.notificationService = notificationService
  }

  @GetMapping
  ResponseEntity<List<Patient>> getPatients() {
    try {
      ResponseEntity.ok(patientService.getPatients())
    } catch (PamException e) {
      ResponseEntity.badRequest().build()
    }
  }

  @PostMapping('/patients/{id}/notify')
  ResponseEntity sendNotification(@PathVariable String id) {
    def patient = patientService.getPatientById(id)
    notificationService.show(patient, "Pam Reminder", "Have you taken your medication today?")
  }

  @PostMapping('/patients/{id}/notify/yes')
  ResponseEntity yesResponse(@PathVariable String id) {
    patientService.incrementPostiveResponse()
  }

  @PostMapping('/patients/{id}/notify/no')
  ResponseEntity noResponse(@PathVariable String id) {
    def patient = patientService.getPatientById(id)
    notificationService.show(patient, "Pam Reminder", "Have you taken your medication today?")
  }
}
