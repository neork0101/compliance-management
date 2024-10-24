package com.in.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.in.auth.dto.CommunicationResponse;
import com.in.auth.dto.ErrorDetails;
import com.in.auth.dto.ResponseDto;
import com.in.auth.service.OtpService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("identitymanagement/api/otp")
@Slf4j
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
    public ResponseEntity<ResponseDto> sendOtpByEmail(@RequestParam String email) {
        try {
            log.info("Sending OTP to email: {}", email);

            // Send the OTP via email
            String emailApiResponse = otpService.sendOtpByEmail(email);

            // Create a response DTO with status, message, and data
            CommunicationResponse<String> response = new CommunicationResponse<>(
                "SUCCESS",
                "OTP has been sent to your email.",
                emailApiResponse // You can include additional data if needed
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Exception while sending OTP email to: {}", email, e);

            // Create an error response DTO with status code, message, and details
            ErrorDetails errorDetails = new ErrorDetails(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Failed to send OTP email",
                e.getMessage()
            );

            return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Send OTP via SMS
    @PostMapping("/send-sms")
    public ResponseEntity<String> sendOtpBySms(@RequestParam String phoneNumber) {
        otpService.sendOtpBySms(phoneNumber);
        return ResponseEntity.ok("OTP has been sent to your phone.");
    }

    /**
     * Validates the OTP provided by the user.
     *
     * @param identifier The identifier (email or phone number) associated with the OTP.
     * @param otp        The OTP to validate.
     * @return ResponseEntity with status and message.
     */
    @PostMapping("/validate")
    public ResponseEntity<ResponseDto> validateOtp(@RequestParam String identifier, @RequestParam String otp) {
        try {
            log.info("Validating OTP for identifier: {}", identifier);

            boolean isValid = otpService.validateOtp(identifier, otp);
            if (isValid) {
                // Create a response DTO with status and message
                CommunicationResponse<Void> response = new CommunicationResponse<>(
                    "SUCCESS",
                    "OTP is valid."
                );

                return ResponseEntity.ok(response);
            } else {
                // Create an error response DTO
                ErrorDetails errorDetails = new ErrorDetails(
                    HttpStatus.BAD_REQUEST.value(),
                    "Invalid or expired OTP.",
                    "The OTP provided is invalid or has expired."
                );

                return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            log.error("Exception occurred while validating OTP for identifier: {}", identifier, e);

            // Create an error response DTO
            ErrorDetails errorDetails = new ErrorDetails(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An error occurred while validating the OTP.",
                e.getMessage()
            );

            return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}