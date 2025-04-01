package com.in.auth.repository;


import org.springframework.data.mongodb.repository.MongoRepository;

import com.in.security.models.Organization;

public interface OrganizationRepository extends MongoRepository<Organization, String> {
}