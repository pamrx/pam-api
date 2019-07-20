package com.daugherty.pam.patient

import groovy.transform.CompileStatic
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.stereotype.Repository

@CompileStatic
@Repository
@RepositoryRestResource(exported = false)
interface PatientMetadataRepository extends MongoRepository<PatientMetadata, String> {
  PatientMetadata findByPatientId(String patientId)
  PatientMetadata findByUsername(String username)
}
