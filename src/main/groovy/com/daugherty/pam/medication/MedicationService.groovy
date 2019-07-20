package com.daugherty.pam.medication

import com.daugherty.pam.exception.ERROR_CODE
import org.springframework.stereotype.Service

@Service
class MedicationService {
  private final MedicationRepository medicationRepository

  MedicationService(final MedicationRepository medicationRepository) {
    this.medicationRepository = medicationRepository
  }

  List<Medication> getMedications() {
    medicationRepository.findAll()
  }

  Medication getMedicationById(String id) {
    def optionalMedication = medicationRepository.findById(id)
    if (optionalMedication.isPresent()) return optionalMedication.get()
    throw PamException(ERROR_CODE.NOT_FOUND)
  }

  List<Medication> searchMedications(String text) {
    return getMedications().findAll { it.title.toLowerCase().matches(".*${text.toLowerCase()}.*") }
  }
}
