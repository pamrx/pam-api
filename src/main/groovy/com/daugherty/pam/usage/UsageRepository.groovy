package com.daugherty.pam.usage

import groovy.transform.CompileStatic
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.stereotype.Repository

@CompileStatic
@Repository
@RepositoryRestResource(exported = false)
interface UsageRepository extends MongoRepository<Usage, String> {
}
