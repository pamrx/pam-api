package com.daugherty.pam.patient

import com.fasterxml.jackson.annotation.JsonProperty
import groovy.transform.CompileStatic
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.springframework.data.mongodb.core.mapping.Document

@ToString
@CompileStatic
@EqualsAndHashCode
@Document(collection = 'patients')
class Patient {
  String title
  String fname
  String mname
  String lname
  String street
  @JsonProperty('postal_code')
  String postalCode
  String city
  @JsonProperty('country_code')
  String countryCode
  @JsonProperty('phone_contact')
  String phoneContact
  String dob
  String sex
  String race
  String ethnicity
  Prescription[] prescriptions
  String notificationToken
}
