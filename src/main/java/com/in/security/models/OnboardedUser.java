package com.in.security.models;


import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Document(collection = "onboarded_users")
@Data
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
    @JsonBackReference
    private Organization organization = new Organization();

    @NotBlank
    private String status;


}
