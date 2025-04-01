package com.in.auth.payload.response;

import java.util.List;

import com.in.auth.dto.ResponseDto;

import io.swagger.v3.oas.annotations.media.Schema;

public class UserInfoResponse implements ResponseDto{
    @Schema(description = "Unique user identifier", example = "6123456789abcdef01234567")
    private String id;

    @Schema(description = "User's username", example = "john_doe")
    private String username;

    @Schema(description = "User's email address", example = "john_doe@example.com")
    private String email;

    @Schema(description = "List of roles assigned to the user", example = "[\"ROLE_USER\", \"ROLE_ADMIN\"]")
    private List<String> roles;

  public UserInfoResponse(String id, String username, String email, List<String> roles) {
    this.id = id;
    this.username = username;
    this.email = email;
    this.roles = roles;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public List<String> getRoles() {
    return roles;
  }
}
