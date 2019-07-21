package com.daugherty.pam.patient

import com.daugherty.pam.emr.EmrService
import groovy.transform.CompileStatic
import org.springframework.stereotype.Service

@Service
@CompileStatic
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

  PatientMetadata getPatientMetadataByPatientId(String patientId) {
    patientMetadataRepository.findByPatientId(patientId)
  }

  Patient getPatientByUsername(String username) {
    def metadata = patientMetadataRepository.findByUsername(username)
    emrService.getPatientById(metadata.patientId)
  }

  PatientPrescription getPatientPrescriptionFromPrescriptionId(String prescriptionId) {
    patientPrescriptionRepository.findByPrescriptionId(prescriptionId)
  }

  void addPrescriptionToPatient(String id, PatientPrescription prescription) {

  }

  void addNotificationTokenToPatient(String id, String token) {
    def metadata = patientMetadataRepository.findByPatientId(id)
    metadata.notificationToken = token
    patientMetadataRepository.save(metadata)
  }
}
