package com.in.auth.payload.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public class LoginRequest {
  @NotBlank
  @Schema(description = "User's username", example = "john_doe")
  private String username;

  @NotBlank
  @Schema(description = "User's password", example = "securePassword123")
  private String password;

  public LoginRequest(String username, String password) {
	  this.username=username;
	  this.password=password;
  }

public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}
