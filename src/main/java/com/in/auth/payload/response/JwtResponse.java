package com.in.auth.payload.response;

import java.util.List;

import com.in.auth.dto.ResponseDto;
import com.in.security.models.Organization;
import com.in.security.models.UserProfile;

import io.swagger.v3.oas.annotations.media.Schema;


public class JwtResponse implements ResponseDto {
    @Schema(description = "JWT token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;

    @Schema(description = "Unique user identifier", example = "6123456789abcdef01234567")
    private String id;

    @Schema(description = "User's username", example = "john_doe")
    private String username;

    @Schema(description = "User's email address", example = "john_doe@example.com")
    private String email;

    @Schema(description = "List of roles assigned to the user", example = "[\"ROLE_USER\", \"ROLE_ADMIN\"]")
    private List<String> roles;

    @Schema(description = "User's profile details")
    private UserProfile userProfile;

    @Schema(description = "User's organization details")
    private Organization organization;

    public JwtResponse(String token, String id, String username, String email, List<String> roles,
                       UserProfile userProfile, Organization organization) {
        this.token = token;
        this.id = id;
        this.username = username;
        this.email = email;
        this.roles = roles;
        this.userProfile = userProfile;
        this.organization = organization;
    }

    // Getters and setters for all fields
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public UserProfile getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(UserProfile userProfile) {
        this.userProfile = userProfile;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }
}
