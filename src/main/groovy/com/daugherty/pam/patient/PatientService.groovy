package com.daugherty.pam.patient

import com.daugherty.pam.emr.EmrService
import org.springframework.stereotype.Service

@Service
class PatientService {
  private final EmrService emrService
  private final PatientMetadataRepository patientMetadataRepository
  private final PatientPrescriptionRepository patientPrescriptionRepository

  PatientService(final EmrService emrService, final PatientMetadataRepository patientMetadataRepository,
                 final PatientPrescriptionRepository patientPrescriptionRepository) {
    this.emrService = emrService
    this.patientPrescriptionRepository = patientPrescriptionRepository
    this.patientMetadataRepository = patientMetadataRepository
  }

  List<Patient> getPatients() {
    emrService.getPatients()
  }

  Patient getPatientById(String id) {
    emrService.getPatientById(id)
  }

  void sync(List<Patient> patients) {
    patients.each {
      def optionalPatient = patientRepository.findById(it.id)
      if (!optionalPatient.isPresent()) patientRepository.save(it)
    }
  }

  void incrementPositiveResponse(String patientId, String medicationId) {
    def patient = getPatientById(patientId)
    patient.prescriptions.find { it.medicationId == medicationId }.positiveResponse++
    patientRepository.save(patient)
   }

  void decrementPositiveResponse(String id, String medicationId) {
    def patient = getPatientById(id)
    patient.prescriptions.find { it.medicationId == medicationId }.negativeResponse++
    patientRepository.save(patient)
  }

  void addPrescriptionToPatient(String id, PatientPrescription prescription) {

  }

  void addNotificationTokenToPatient(String id, String token) {
    def metadata = patientMetadataRepository.findByPatientId(id)
    metadata.notificationToken = token
    patientMetadataRepository.save(metadata)
  }
}
