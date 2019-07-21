package com.daugherty.pam.patient

import groovy.transform.CompileStatic
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@ToString
@CompileStatic
@EqualsAndHashCode
@Document(collection = 'patient_prescriptions')
class PatientPrescription {
  String id
  String patient_id
  String start_date //yyyy-mm-dd
  String drug // name of drug
  PRESCRIPTION_FORM form // form of the drug (capsule, pill, puff, etc)
  String dosage // number - how many per
  String quantity //number - how many per initial/refill
  String size // number - how many of unit (e.g. 100 mg)
  PRESCRIPTION_UNIT unit // number - code for unit of measure (mg, mcg, etc)
  PRESCRIPTION_ROUTE route // number - code for how to take medication (orally, applied to affected area, etc)
  PRESCRIPTION_INTERVAL interval // how often to take med (daily, as-needed, etc)
  String refills // number - how many refills
  String per_refill // number - quantity per refill
  String filled_date
  Integer adherence
}
