package com.in.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.in.auth.service.OtpService;

@RestController
@RequestMapping("identitymanagement/api/otp")
public class OtpController {

    @Autowired
    private OtpService otpService;

    /**
     * Send OTP via email.
     *
     * @param email The email address to send the OTP to.
     * @return ResponseEntity with status and message.
     */
    @PostMapping("/send-email")
    public ResponseEntity<String> sendOtpByEmail(@RequestParam String email) {
        try {
            String emailApiResponse = otpService.sendOtpByEmail(email);
            return ResponseEntity.ok("OTP has been sent to your email. " + emailApiResponse);
        } catch (Exception e) {
            System.out.println("Exception while sending OTP email: " + e);
            return ResponseEntity.status(500).body("Failed to send OTP email");
        }
    }

    // Send OTP via SMS
    @PostMapping("/send-sms")
    public ResponseEntity<String> sendOtpBySms(@RequestParam String phoneNumber) {
        otpService.sendOtpBySms(phoneNumber);
        return ResponseEntity.ok("OTP has been sent to your phone.");
    }

    // Validate OTP
    @PostMapping("/validate")
    public ResponseEntity<String> validateOtp(@RequestParam String identifier, @RequestParam String otp) {
        boolean isValid = otpService.validateOtp(identifier, otp);
        if (isValid) {
            return ResponseEntity.ok("OTP is valid.");
        } else {
            return ResponseEntity.status(400).body("Invalid or expired OTP.");
        }
    }
}
