package com.daugherty.pam.patient

import com.daugherty.pam.emr.EmrService
import com.daugherty.pam.exception.ERROR_CODE
import com.daugherty.pam.exception.PamException
import groovy.transform.CompileStatic
import org.springframework.http.HttpHeaders
import org.springframework.scheduling.annotation.Scheduled
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

  @Scheduled(fixedRateString = '10000')
  void syncPatientPrescriptions() {
    getPatients().each { patient ->
      evaluatePatientPrescriptions(patient)
    }
  }

  void evaluatePatientPrescriptions(Patient patient) {
    emrService.getPrescriptionsForPatient(patient.pid).each { prescription ->
      if (!getPatientPrescriptionByPrescriptionId(prescription.id)) {
        storePatientPrescription(prescription)
      }
    }
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

  Patient updatePatientPrescriptionAdherenceScore(String patientId, Float score) {
    def patient = emrService.getPatientById(patientId)
    if (!patient) {
      throw new PamException(ERROR_CODE.NOT_FOUND)
    }

    patient.adherence = score

    updatePatient(patient)
  }

  Patient updatePatient(Patient patient) {
    emrService.updatePatient(patient)
  }

  PatientPrescription getPatientPrescriptionByPrescriptionId(String prescriptionId) {
    patientPrescriptionRepository.findById(prescriptionId).orElse(null)
  }

  void storePatientPrescription(PatientPrescription prescription) {
    patientPrescriptionRepository.insert(prescription)
  }

  List<PatientPrescription> getPatientPrescriptions(String patientId) {
    emrService.getPrescriptionsForPatient(patientId)
  }

  void addPrescriptionToPatient(String patientId, PatientPrescription prescription) {
    emrService.addPrescriptionToPatient(patientId, prescription)
  }

  void addNotificationTokenToPatient(String id, String token) {
    def metadata = patientMetadataRepository.findByPatientId(id)
    metadata.notificationToken = token
    patientMetadataRepository.save(metadata)
  }
}
