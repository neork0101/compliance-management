package com.in.compliance.payload.response;

import com.in.compliance.dto.ResponseDto;

public class MessageResponse implements ResponseDto{
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
