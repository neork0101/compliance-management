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

import com.in.compliance.dto.credential.ProviderDto;
import com.in.compliance.exception.NotFoundExceptions;
import com.in.compliance.models.credential.Credential;
import com.in.compliance.models.credential.Provider;
import com.in.compliance.repository.credential.ProviderRepository;
import com.in.compliance.service.credential.ProviderService;

@RestController
@RequestMapping("compliancemanagement/provider")
public class ProviderController {
	
	private static final Logger LOG = LoggerFactory.getLogger(ProviderController.class);

    @Autowired
    private ProviderService providerService;
    
    @Autowired
    private ProviderRepository providerRepository;

    // GET /providers: List all providers
	/*
	 * @GetMapping public ResponseEntity<List<Provider>> getAllProviders() {
	 * List<Provider> providers = providerService.findAllProvider(); return new
	 * ResponseEntity<>(providers, HttpStatus.OK); }
	 */

    // GET /providers/{id}: Get details of a provider
    @GetMapping("/{id}")
    public ResponseEntity<ProviderDto> getProviderById(@PathVariable String id) {

        try {
        	ProviderDto credential = providerService.getProviderById(id);
        return ResponseEntity.ok(credential);
    } catch (NotFoundExceptions ex) {
    	LOG.error("Exception:getCredentialById ", ex);
        return ResponseEntity.notFound().build();
    } 
    }

    // POST /providers: Add a new provider
    @PostMapping
    public ResponseEntity<ProviderDto> addProvider(@RequestBody ProviderDto provider) {
    	ProviderDto savedProviderDto = providerService.addProvider(provider);
        return new ResponseEntity<>(savedProviderDto, HttpStatus.CREATED);
    }

    // PUT /providers/{id}: Update provider information
    @PutMapping("/{id}")
    public ResponseEntity<Provider> updateProvider(@PathVariable String id, @RequestBody ProviderDto providerDetails) {
       Provider providerOptional = providerService.updateProvider(id, providerDetails);

        if (providerOptional !=null){
        
            return new ResponseEntity<>(providerOptional, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
    }
    
    @GetMapping
    public ResponseEntity<List<Provider>> getProvider(
            @RequestParam(value = "name", required = false) String name) {

        List<Provider> Providerp = providerService.getProviders(name);

        return new ResponseEntity<>(Providerp, HttpStatus.OK);
    }
    
 // Soft DELETE /credentials/{id}: Soft delete a Provider
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> softDeleteCredential(@PathVariable String id) {
        Optional<Provider> providerOptional = providerRepository.findById(id);

        if (providerOptional.isPresent()) {
        	Provider provider = providerOptional.get();
        	provider.setDeleted(true);
            providerRepository.save(provider);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}

