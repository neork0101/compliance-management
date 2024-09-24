package com.in.compliance.dto.credential;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.data.annotation.Transient;

import com.in.compliance.encrypt.AES256Util;
import com.in.compliance.encrypt.DataAnonymizationUtil;

import lombok.Data;

@Data
public class CredentialDto {

	private String id;

    private String provider; // PHI field that needs anonymization

    private String credentialType;

    @Transient
    private String issuer;  // Transient field

    private String encryptedIssuer;  // Encrypted field

    @Transient
    private LocalDate issueDate;  // Transient field

    private String encryptedIssueDate;  // Encrypted field
    
    private LocalDate expiryDate;

    private String status;

    private List<String> validStateProvince;

    private boolean verificationStatus;    
    
    private boolean deleted = false; // New field for soft delete
    
    public String getProvider() {
        return DataAnonymizationUtil.anonymizeProviderName(provider);  // Anonymized provider name
    }
    
    // Encrypt issuer when setting it
    public String getIssuer() {
        return AES256Util.decrypt(encryptedIssuer);
    }

    public void setIssuer(String issuer) {
        this.encryptedIssuer = AES256Util.encrypt(issuer);
    }

    // Encrypt issueDate when setting it
    public LocalDate getIssueDate() {
        return LocalDate.parse(AES256Util.decrypt(encryptedIssueDate), DateTimeFormatter.ISO_DATE);
    }

    public void setIssueDate(LocalDate issueDate) {
        this.encryptedIssueDate = AES256Util.encrypt(issueDate.toString());
    }


}
