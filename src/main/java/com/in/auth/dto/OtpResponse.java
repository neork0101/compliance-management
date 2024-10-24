
package com.in.auth.dto;

import lombok.Data;

@Data
public class OtpResponse {
    private String email;
    private String expiryTime;
    
    public OtpResponse(String email, String expiryTime) {
        this.email = email;
        this.expiryTime = expiryTime;
    }
}