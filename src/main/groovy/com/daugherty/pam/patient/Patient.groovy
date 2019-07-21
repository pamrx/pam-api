package com.daugherty.pam.patient

import com.fasterxml.jackson.annotation.JsonProperty
import groovy.transform.CompileStatic
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@ToString
@CompileStatic
@EqualsAndHashCode
class Patient {
  String pid
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
  PatientPrescription[] prescriptions
  String notificationToken
  Float adherence
}
