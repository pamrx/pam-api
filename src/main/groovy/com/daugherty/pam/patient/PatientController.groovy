package com.daugherty.pam.patient

import com.daugherty.pam.exception.PamException
import com.daugherty.pam.notification.NotificationService
import groovy.util.logging.Slf4j
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

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

  @PostMapping('/patients/{id}')
  ResponseEntity sendNotification(@PathVariable String id, @RequestBody String message) {
    def patient = patientService.getPatientById(id)
    notificationService.show(patient, "Test Title", message)
  }
}
