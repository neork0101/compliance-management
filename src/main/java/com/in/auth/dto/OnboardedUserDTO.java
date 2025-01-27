package com.in.auth.dto;


import java.util.Set;

import com.in.security.models.Organization;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class OnboardedUserDTO {

    private String id;

    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    private Set<String> roles;

    private Organization organization = new Organization();

    private String status;

}
