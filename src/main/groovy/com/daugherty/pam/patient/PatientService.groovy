package com.daugherty.pam.patient

import org.springframework.stereotype.Service

@Service
class PatientService {
  private final PatientRepository patientRepository

  PatientService(final PatientRepository patientRepository) {
    this.patientRepository = patientRepository
  }

  List<Patient> getPatients() {
    patientRepository.findAll()
  }
}
