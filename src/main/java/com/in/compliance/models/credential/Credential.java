package com.in.compliance.models.credential;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.in.compliance.encrypt.AES256Util;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Document(collection = "Credentials")
public class Credential {

    @Id
    private String id;

    private String provider;

    private String credentialType;

    private LocalDate expiryDate;

    @Transient
    private String issuer;

    @Field(name = "encrypted_issuer")
    private String encryptedIssuer;

    @Transient
    private LocalDate issueDate;

    @Field(name = "encrypted_issue_date")
    private String encryptedIssueDate;
    
    private String status;

   // @ElementCollection
    private List<String> validStateProvince;

    private boolean verificationStatus;    

    private boolean deleted = false; // New field for soft delete
    
    public String getIssuer() {
        return AES256Util.decrypt(encryptedIssuer);
    }

    public void setIssuer(String issuer) {
        this.encryptedIssuer = AES256Util.encrypt(issuer);
    }

    public LocalDate getIssueDate() {
        return LocalDate.parse(AES256Util.decrypt(encryptedIssueDate), DateTimeFormatter.ISO_DATE);
    }

    public void setIssueDate(LocalDate issueDate) {
        this.encryptedIssueDate = AES256Util.encrypt(issueDate.toString());
    }

    
}
