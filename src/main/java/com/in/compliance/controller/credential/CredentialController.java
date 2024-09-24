package com.in.compliance.controller.credential;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.in.compliance.dto.credential.CredentialDto;
import com.in.compliance.exception.NotFoundExceptions;
import com.in.compliance.models.credential.Credential;
import com.in.compliance.repository.credential.CredentialRepository;
import com.in.compliance.service.credential.CredentialService;

@RestController
@RequestMapping("compliancemanagement/credentials")
public class CredentialController {
	
	private static final Logger LOG = LoggerFactory.getLogger(CredentialController.class);

    @Autowired
    private CredentialRepository credentialRepository;
    @Autowired
    private CredentialService credentialService;

    // POST /credentials: Add a new credential
    @PostMapping
    public ResponseEntity<CredentialDto> addCredential(@RequestBody CredentialDto credential) {
    	CredentialDto savedCredential = credentialService.addCredential(credential);
        return new ResponseEntity<>(savedCredential, HttpStatus.CREATED);
    }

    // GET /credentials: Fetch credentials with filters (e.g., by status, provider, state)
    @GetMapping
    public ResponseEntity<List<Credential>> getCredentials(
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "provider", required = false) String provider,
            @RequestParam(value = "state", required = false) String state) {

        List<Credential> credentials = credentialService.getCredentials(status, provider, state);


        return new ResponseEntity<>(credentials, HttpStatus.OK);
    }

    // GET /credentials/{id}: Fetch a specific credential by ID
    @GetMapping("/{id}")
    public ResponseEntity<CredentialDto> getCredentialById(@PathVariable String id) {
       

        try {
        	 CredentialDto credential = credentialService.getCredentialById(id);
        return ResponseEntity.ok(credential);
    } catch (NotFoundExceptions ex) {
    	LOG.error("Exception:getCredentialById ", ex);
        return ResponseEntity.notFound().build();
    } 
        
	/*
	 * credential.map(ResponseEntity::ok) .orElseGet(() -> new
	 * ResponseEntity<>(HttpStatus.NOT_FOUND));
	 */
    }

    // PUT /credentials/{id}: Update a credential (e.g., change status, update expiry)
    @PutMapping("/{id}")
    public ResponseEntity<Credential> updateCredential(@PathVariable String id, @RequestBody CredentialDto credentialDetails) {
       
    	Credential updatedCredential = credentialService.updateCredential(id, credentialDetails);
        if (updatedCredential != null) {
        	
            return new ResponseEntity<>(updatedCredential, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

 // Soft DELETE /credentials/{id}: Soft delete a credential
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> softDeleteCredential(@PathVariable String id) {
        Optional<Credential> credentialOptional = credentialRepository.findById(id);

        if (credentialOptional.isPresent()) {
            Credential credential = credentialOptional.get();
            credential.setDeleted(true);
            credentialRepository.save(credential);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
