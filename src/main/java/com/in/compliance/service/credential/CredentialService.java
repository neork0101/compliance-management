package com.in.compliance.service.credential;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.in.compliance.dto.credential.CredentialDto;
import com.in.compliance.encrypt.DataAnonymizationUtil;
import com.in.compliance.exception.NotFoundExceptions;
import com.in.compliance.models.credential.Credential;
import com.in.compliance.repository.credential.CredentialRepository;

@Service
public class CredentialService {

	@Autowired
	private CredentialRepository credentialRepository;

	public CredentialDto addCredential(CredentialDto credentialDto) {
		 Credential credential = new  Credential ();
		Credential savedCredential = credentialRepository.save(dtoToModel(credentialDto, false,credential));
		credential.setId(savedCredential.getId());
		return credentialDto;
	}

	public List<Credential> getCredentials(String status, String provider, String state) {

		List<Credential> credentials;

		if (status != null) {
			credentials = credentialRepository.findByStatus(status);
		} else if (provider != null) {
			credentials = credentialRepository.findByProvider(provider);
		} else if (state != null) {
			List<String> states = new ArrayList<String>();
			states.add(state);
			credentials = credentialRepository.findByValidStateProvinceIn(states);
		} else {
			credentials = credentialRepository.findAll();
		}

		return credentials;
	}

	public CredentialDto getCredentialById(String id) throws NotFoundExceptions {
		Optional<Credential> credential = credentialRepository.findById(id);
		if (credential.isPresent()) {
			return modelToDto(credential.get());
		} else {
			throw new NotFoundExceptions("Credential not found with ID: " + id);
		}
	}

	
	public Credential updateCredential(String id, CredentialDto credentialDto) {
		
		 Optional<Credential> credentialOptional = credentialRepository.findById(id);
		 Credential updatedCredential=null;
		 
	        if (credentialOptional.isPresent()) {
	        	Credential credential = credentialOptional.get();
	        	dtoToModel(credentialDto, true,credential);
	            updatedCredential = credentialRepository.save(credential);
	        }
	        
	        return updatedCredential;
	}
	
	
	private Credential dtoToModel(CredentialDto dto, boolean isUpdate, Credential credential) {

		if (!isUpdate)
			credential.setId(dto.getId());
		
		if (dto.getCredentialType() != null)
			credential.setCredentialType(dto.getCredentialType());
		if (dto.getIssueDate() != null)
			credential.setIssueDate(dto.getIssueDate());
		if (dto.getIssuer() != null)
			credential.setIssuer(dto.getIssuer());
		if (dto.getExpiryDate() != null)
			credential.setExpiryDate(dto.getExpiryDate());
		if (dto.getProvider() != null)
			credential.setProvider(dto.getProvider());
		if (dto.getValidStateProvince() != null)
			credential.setValidStateProvince(dto.getValidStateProvince());
		if (dto.getStatus() != null)
			credential.setStatus(dto.getStatus());

		return credential;

	}

	private CredentialDto modelToDto(Credential model) {
		CredentialDto credential = new CredentialDto();
		credential.setId(model.getId());
		credential.setCredentialType(model.getCredentialType());
		credential.setIssueDate(model.getIssueDate());
		credential.setIssuer(model.getIssuer());
		credential.setExpiryDate(model.getExpiryDate());
		//credential.setProvider(model.getProvider());
		//credential.setProvider(DataAnonymizationUtil.anonymizeProviderName(model.getProvider()));
		//credential.setIssuer(DataAnonymizationUtil.tokenizeData(credential.getIssuer()));


		credential.setValidStateProvince(model.getValidStateProvince());
		credential.setStatus(model.getStatus());

		return credential;

	}
}
