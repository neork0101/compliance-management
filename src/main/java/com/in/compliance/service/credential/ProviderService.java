package com.in.compliance.service.credential;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.in.compliance.dto.credential.ProviderDto;
import com.in.compliance.exception.NotFoundExceptions;
import com.in.compliance.models.credential.Provider;
import com.in.compliance.repository.credential.ProviderRepository;

@Service
public class ProviderService {

	@Autowired
	private ProviderRepository providerRepository;
	
	public List<Provider> findAllProvider() {
		
		 List<Provider> providers = providerRepository.findAll();
			return providers;
	}

	public ProviderDto addProvider(ProviderDto providerDto) {
		Provider provider = new  Provider ();
		Provider savedProvider = providerRepository.save(dtoToModel(providerDto, false,provider));
		provider.setId(savedProvider.getId());
		return providerDto;
	}

	public List<Provider> getProviders(String name) {

		List<Provider> providers;

		if (name != null) {
			providers = providerRepository.findByName(name);
		
		} else {
			providers = providerRepository.findByDeletedFalse();
		}

		return providers;
	}

	public ProviderDto getProviderById(String id) throws NotFoundExceptions {
		Optional<Provider> provider = providerRepository.findById(id);
		if (provider.isPresent()) {
			return modelToDto(provider.get());
		} else {
			throw new NotFoundExceptions("Provider not found with ID: " + id);
		}
	}

	
	public Provider updateProvider(String id, ProviderDto providerDto) {
		
		 Optional<Provider> providerOptional = providerRepository.findById(id);
		 Provider updatedProvider=null;
		 
	        if (providerOptional.isPresent()) {
	        	Provider provider = providerOptional.get();
	        	dtoToModel(providerDto, true,provider);
	        	updatedProvider = providerRepository.save(provider);
	        }
	        
	        return updatedProvider;
	}
	
	
	private Provider dtoToModel(ProviderDto dto, boolean isUpdate, Provider provider) {

		if (!isUpdate)
			provider.setId(dto.getId());
		
		if (dto.getAddress() != null)
			provider.setAddress(dto.getAddress());
		if (dto.getContactInfo() != null)
			provider.setContactInfo(dto.getContactInfo());
		if (dto.getCreatedAt() != null)
			provider.setCreatedAt(dto.getCreatedAt());
		if (dto.getName() != null)
			provider.setName(dto.getName());
		if (dto.getUpdatedAt() != null)
			provider.setUpdatedAt(dto.getUpdatedAt());
		

		return provider;

	}

	private ProviderDto modelToDto(Provider model) {
		ProviderDto providerDto = new ProviderDto();
		providerDto.setId(model.getId());
		providerDto.setAddress(model.getAddress());
		providerDto.setContactInfo(model.getContactInfo());
		providerDto.setCreatedAt(model.getCreatedAt());
		providerDto.setName(model.getName());
		providerDto.setUpdatedAt(model.getUpdatedAt());
		
		return providerDto;

	}
}
