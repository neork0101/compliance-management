package com.in.auth.dto;

import lombok.Data;

@Data
public class PasswordResetResponse {
    private String email;
    private String resetLink;

    public PasswordResetResponse(String email, String resetLink) {
        this.email = email;
        this.resetLink = resetLink;
    }
}
