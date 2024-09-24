package com.in.compliance.repository.credential;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.in.compliance.models.credential.Alert;

public interface AlertRepository  extends MongoRepository<Alert, String> {
	
	  List<Alert> findByReadFalse();

}
