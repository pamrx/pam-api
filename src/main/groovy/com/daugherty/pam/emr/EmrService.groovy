package com.daugherty.pam.emr

import com.daugherty.pam.medication.MedicationService
import com.daugherty.pam.patient.Patient
import com.daugherty.pam.patient.PatientService
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

import javax.annotation.PostConstruct

@Service
class EmrService {
  private final RestTemplate restTemplate
  private final PatientService patientService
  private final MedicationService medicationService
  private String emrToken

  EmrService(final RestTemplate restTemplate, final PatientService patientService, final MedicationService medicationService) {
    this.restTemplate = restTemplate
    this.patientService = patientService
    this.medicationService = medicationService
  }

  @PostConstruct
  @Scheduled(fixedRateString = '3600')
  void getEmrToken() {
    emrToken = restTemplate.postForEntity(
        'http://159.65.225.138/apis/api/auth',
        {
          grant_type = "password"
          username = "admin"
          password = "pamrx"
          scope = "default"
        },
        String)
  }

  @Scheduled(fixedRateString = '300')
  void syncPatients() {
    def headers = new HttpHeaders().set('authorization', emrToken)
    def patients = restTemplate.exchange('http://159.65.225.138/apis/api/patient', HttpMethod.GET,
        new HttpEntity<String>(headers), PatientList).getBody().patients
    patientService.sync(patients)
  }
}

class PatientList {
  List<Patient> patients
}
