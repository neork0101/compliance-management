package com.in.compliance.repository.credential;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.in.compliance.models.credential.Provider;

public interface ProviderRepository extends MongoRepository<Provider, String> {

	List<Provider> findByName(String status);
    List<Provider> findByDeletedFalse();  // Fetch only non-deleted Provider

}
