package com.daugherty.pam.notification


import groovy.transform.CompileStatic
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.stereotype.Repository

@CompileStatic
@Repository
@RepositoryRestResource(exported = false)
interface PatientNotificationRepository extends MongoRepository<PatientNotification, String> {
  PatientNotification findTopByPatientIdAndPrescriptionIdOrderByLastNotificationTime(String patientId, String prescriptionId)
}