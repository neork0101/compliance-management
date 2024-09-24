package com.in.compliance.models.credential;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.in.compliance.encrypt.DataAnonymizationUtil;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Document(collection = "Providers")
public class Provider {

	@Id
	private String id;
	private String name;
	private String address;
	private String contactInfo;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
    private boolean deleted = false; //  field for soft delete
    
 // Mask the contact info before returning it
    public String getContactInfo() {
        return DataAnonymizationUtil.maskContactInfo(contactInfo);
    }

}
