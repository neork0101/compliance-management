package com.in.auth.service;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.in.security.util.OtpUtil;

@Service
public class OtpService {



    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final int EXPIRATION_MINUTES = 5;
    private static final String OTP_PREFIX = "otp_"; // Redis key prefix for OTPs

    // Generate OTP and send it via email, storing in Redis with an expiration
    public String sendOtpByEmail(String email) {
    	
        String otp = OtpUtil.generateOTP();
        long expirationTime = OtpUtil.getExpirationTime(EXPIRATION_MINUTES);

        // Store OTP in Redis with expiration
        redisTemplate.opsForValue().set(OTP_PREFIX + email, otp, EXPIRATION_MINUTES, TimeUnit.MINUTES);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Your OTP Code");
        message.setText("Your OTP code is: " + otp + "\nIt will expire in " + EXPIRATION_MINUTES + " minutes.");
        //mailSender.send(message);
        return message.getText();
        
    }

    // Optional: Send OTP via SMS using Twilio or any other SMS service
    public void sendOtpBySms(String phoneNumber) {
        String otp = OtpUtil.generateOTP();
        redisTemplate.opsForValue().set(OTP_PREFIX + phoneNumber, otp, EXPIRATION_MINUTES, TimeUnit.MINUTES);

        // Send OTP via Twilio (or any other SMS provider)
        // Twilio or another service can be used to send OTP via SMS.
    }

    // Validate OTP
    public boolean validateOtp(String identifier, String otp) {
        String storedOtp = redisTemplate.opsForValue().get(OTP_PREFIX + identifier);
        return storedOtp != null && storedOtp.equals(otp);
    }
}
