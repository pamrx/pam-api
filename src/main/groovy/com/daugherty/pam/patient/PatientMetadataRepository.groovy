package com.daugherty.pam.patient

import com.daugherty.pam.medication.PatientMetadata
import groovy.transform.CompileStatic
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.stereotype.Repository

@CompileStatic
@Repository
@RepositoryRestResource(exported = false)
interface PatientMetadataRepository extends MongoRepository<PatientMetadata, String> {
  PatientMetadata findByPatientId(String patientId)
}
