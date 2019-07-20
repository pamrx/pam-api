package com.daugherty.pam.patient

import com.daugherty.pam.exception.ERROR_CODE
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

  Patient getPatientById(String id) {
    def optionalPatient = patientRepository.findById(id)
    if (optionalPatient.isPresent()) return optionalPatient.get()
    throw PamException(ERROR_CODE.NOT_FOUND)
  }
  /*
   void incrementPositiveResponse(String id) {
     def patient = getPatientById(id)
     patient.prescriptions.each { it.positiveResponse++ }
     patientRepository.save(patient)
   }

   void incrementPositiveResponse(String id) {
     def patient = getPatientById(id)
     patient.prescriptions.each { it.positiveResponse++ }
     patientRepository.save(patient)
   }*/
}
