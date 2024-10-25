package com.in.auth.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommunicationResponse<T> implements ResponseDto {
    private String status;
    private String message;
    private Object data;
    
    public CommunicationResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }
    
    public CommunicationResponse(String status, String message, Object data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }
}