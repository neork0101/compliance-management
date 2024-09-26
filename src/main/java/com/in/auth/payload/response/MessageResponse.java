package com.in.auth.payload.response;

import com.in.auth.dto.ResponseDto;

import io.swagger.v3.oas.annotations.media.Schema;

public class MessageResponse implements ResponseDto{
    @Schema(description = "Response message", example = "User registered successfully!")
    private String message;

  public MessageResponse(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
