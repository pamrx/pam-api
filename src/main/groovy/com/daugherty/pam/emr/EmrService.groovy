package com.daugherty.pam.emr


import com.daugherty.pam.patient.Patient
import com.daugherty.pam.patient.PatientService
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

import javax.annotation.PostConstruct

@Service
class EmrService {
  private final RestTemplate restTemplate
  private final PatientService patientService
  private ResponseEntity<LinkedHashMap<String, String>> emrToken

  EmrService(final RestTemplate restTemplate) {
    this.restTemplate = restTemplate
    this.patientService = patientService
  }

  @PostConstruct
  @Scheduled(fixedRateString = '350000')
  void getEmrToken() {
    def body = new HashMap()
    body.put('grant_type', 'password')
    body.put('username', 'admin')
    body.put('password', 'pamrx')
    body.put('scope', 'default')
    emrToken = restTemplate.postForEntity('http://159.65.225.138/apis/api/auth', body, Object)
  }

  @Scheduled(fixedRateString = '10000')
  void syncPatientPrescriptions() {
    def headers = new HttpHeaders()
    headers.setBearerAuth(emrToken.body.get('access_token'))
    def patients = restTemplate.exchange('http://159.65.225.138/apis/api/patient', HttpMethod.GET,
        new HttpEntity<String>(headers), Patient[]).getBody().toList()
    //patientService.sync(patients)
  }

  List<Patient> getPatients() {
    def headers = new HttpHeaders()
    headers.setBearerAuth(emrToken.body.get('access_token'))
    restTemplate.exchange('http://159.65.225.138/apis/api/patient', HttpMethod.GET,
        new HttpEntity<String>(headers), Patient[]).getBody()
  }

  Patient getPatientById(String id) {
    def headers = new HttpHeaders()
    headers.setBearerAuth(emrToken.body.get('access_token'))
    restTemplate.exchange("http://159.65.225.138/apis/api/patient/${id}", HttpMethod.GET,
        new HttpEntity<String>(headers), Patient).getBody() as Patient
  }


}
