package com.daugherty.pam.patient

import groovy.transform.CompileStatic
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@ToString
@CompileStatic
@EqualsAndHashCode
class Prescription {
  String medicationId
  String begdate
  String enddate
  String dosage
  Integer frequency
  FREQUENCY_UNIT frequencyUnit
  String doses
  Integer positiveResponse
  Integer negativeResponse
}
