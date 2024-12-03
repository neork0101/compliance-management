package com.in.security.models;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Document(collection = "onboarded_users")
public class OnboardedUser {
    @Id
    private String id;
    
    @NotBlank
    @Size(max = 50)
    @Email
    private String email;
    
    @DBRef
    private Set<Role> roles = new HashSet<>();
    
    @DBRef
    private Organization organization = new Organization();
    
    @NotBlank
    private String status;

    
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Set<Role> getRoles() {
        return roles;
      }

      public void setRoles(Set<Role> roles) {
        this.roles = roles;
      }
      public Organization getOrganization() {
          return organization;
      }
      public void setOrganization(Organization organization) {
          this.organization = organization;
      }
}
