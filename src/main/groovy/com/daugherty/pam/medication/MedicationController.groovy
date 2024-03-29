package com.daugherty.pam.medication

import com.daugherty.pam.exception.PamException
import groovy.util.logging.Slf4j
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Slf4j
@RestController
class MedicationController {
  private final MedicationService medicationService

  MedicationController(final MedicationService medicationService) {
    this.medicationService = medicationService
  }

  @GetMapping('/medications')
  ResponseEntity<List<Medication>> getMedications(@RequestParam(required = false) String title) {
    try {
      if (title) ResponseEntity.ok(medicationService.searchMedications(title))
      else ResponseEntity.ok(medicationService.getMedications())
    } catch (PamException e) {
      ResponseEntity.badRequest().build()
    }
  }

  @GetMapping('/medications/{id}')
  ResponseEntity<Medication> getMedication(@PathVariable String id) {
    try {
      ResponseEntity.ok(medicationService.getMedicationById(id))
    } catch (PamException e) {
      ResponseEntity.notFound().build()
    }
  }
}
