package com.daugherty.pam.patient

import com.daugherty.pam.exception.PamException
import groovy.util.logging.Slf4j
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@Slf4j
@RestController('/patients')
class PatientController {
  private final PatientService patientService

  PatientController(final PatientService patientService) {
    this.patientService = patientService
  }

  @GetMapping
  ResponseEntity<List<Patient>> getPatients() {
    try {
      ResponseEntity.ok(patientService.getPatients())
    } catch (PamException e) {
      ResponseEntity.badRequest().build()
    }
  }
}
