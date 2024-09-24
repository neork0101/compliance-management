package com.in.compliance.repository.credential;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.in.compliance.models.credential.Credential;

@Repository
public interface CredentialRepository extends MongoRepository<Credential, String> {
    List<Credential> findByStatus(String status);
    List<Credential> findByProvider(String provider);
    //List<Credential> findByState(String state);
    List<Credential> findByValidStateProvinceIn(List<String> validStateProvince);
    List<Credential> findByDeletedFalse();  // Fetch only non-deleted credentials

}
