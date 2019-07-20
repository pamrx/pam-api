package com.daugherty.pam.patient

import groovy.transform.CompileStatic
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@ToString
@CompileStatic
@EqualsAndHashCode
@Document(collection = 'patient_metadata')
class PatientMetadata {
  @Id
  String id
  String patientId
  String username
  String notificationToken
}
