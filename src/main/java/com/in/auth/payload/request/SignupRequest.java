package com.in.auth.payload.request;

import java.util.HashSet;
import java.util.Set;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class SignupRequest {
  @NotBlank
  @Size(min = 3, max = 20)
  @Schema(description = "Desired username", example = "new_user")
  private String username;

  @NotBlank
  @Size(max = 50)
  @Email
  @Schema(description = "User's email address", example = "new_user@example.com")
  private String email;

  @Schema(description = "Roles to assign to the user", example = "[\"user\", \"admin\"]")
  private Set<String> roles;

  @NotBlank
  @Size(min = 6, max = 40)
  @Schema(description = "Desired password", example = "StrongPass!123")
  private String password;

  public SignupRequest(String username, String email, HashSet roles) {
	this.username=username;
	this.email=email;
	this.roles=roles;
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

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public Set<String> getRoles() {
    return this.roles;
  }

  public void setRole(Set<String> roles) {
    this.roles = roles;
  }
}
