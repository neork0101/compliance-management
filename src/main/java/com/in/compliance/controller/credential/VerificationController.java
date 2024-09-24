package com.in.compliance.controller.credential;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.in.compliance.models.credential.Credential;
import com.in.compliance.repository.credential.CredentialRepository;

@RestController
@RequestMapping("compliancemanagement/credentials")
public class VerificationController {

    @Autowired
    private CredentialRepository credentialRepository;

    // POST /credentials/{id}/verify: Verify a credential and update its status
    @PostMapping("/{id}/verify")
    public ResponseEntity<Credential> verifyCredential(@PathVariable String id) {
        Optional<Credential> credentialOptional = credentialRepository.findById(id);

        if (credentialOptional.isPresent()) {
            Credential credential = credentialOptional.get();
            credential.setVerificationStatus(true);
            credential.setStatus("Verified");

            Credential updatedCredential = credentialRepository.save(credential);
            return new ResponseEntity<>(updatedCredential, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // GET /credentials/{id}/verification-status: Check the verification status of a credential
    @GetMapping("/{id}/verification-status")
    public ResponseEntity<Boolean> checkVerificationStatus(@PathVariable String id) {
        Optional<Credential> credentialOptional = credentialRepository.findById(id);

        if (credentialOptional.isPresent()) {
            boolean verificationStatus = credentialOptional.get().isVerificationStatus();
            return new ResponseEntity<>(verificationStatus, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}

