package com.in.auth.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.in.auth.repository.UserRepository;
import com.in.security.models.User;
import com.in.security.util.OtpUtil;

@Service
public class OtpService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private UserRepository userRepository;

    private static final int EXPIRATION_MINUTES = 5;
    private static final String OTP_PREFIX = "otp_"; // Redis key prefix for OTPs

    @Value("${email.api.base-url}")
    private String emailApiBaseUrl;

    @Autowired
    private WebClient.Builder webClientBuilder;

    /**
     * Generate OTP and send it via email, storing in Redis with an expiration.
     *
     * @param email The email address to send the OTP to.
     * @return The response from the email API.
     */
    public String sendOtpByEmail(String email) {

        // Generate a random OTP
        String otp = OtpUtil.generateOTP();

        // Store OTP in Redis with expiration
        redisTemplate.opsForValue().set(OTP_PREFIX + email, otp, EXPIRATION_MINUTES, TimeUnit.MINUTES);

        // Get user by email to retrieve the username
        User user = userRepository.findByEmail(email);
        String username = (user != null) ? user.getUsername() : "";

        // Prepare request body for email API
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("email", email);
        requestBody.put("otp", otp);
        requestBody.put("action", "2FA");
        requestBody.put("year", String.valueOf(LocalDateTime.now().getYear()));
        requestBody.put("username", username);
        requestBody.put("expiryTime", String.valueOf(EXPIRATION_MINUTES));

        // Make HTTP call to email API using WebClient
        WebClient webClient = webClientBuilder.baseUrl(emailApiBaseUrl).build();

        String response = webClient.post()
                .uri("/otp")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return response;
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
