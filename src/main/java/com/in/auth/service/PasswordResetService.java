package com.in.auth.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.in.auth.repository.UserRepository;
import com.in.security.models.User;

@Service
public class PasswordResetService {

    @Autowired
    private UserRepository userRepository;

    @Value("${email.api.base-url}")
    private String emailApiBaseUrl;

    @Value("${email.api.password-reset-url}")
    private String passwordResetUrl;

    @Autowired
    private WebClient.Builder webClientBuilder;

    /**
     * Initiates the password reset process by sending a reset link via email.
     *
     * @param email The email address of the user requesting password reset.
     * @return The response from the email API.
     * @throws Exception If the user is not found.
     */
    public String initiatePasswordReset(String email) throws Exception {
        // Verify if the email exists in the database
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new Exception("User with this email not found");
        }

        // Generate a password reset token
        String resetToken = UUID.randomUUID().toString();

        // Save the reset token and its expiration to the user's record
        user.setResetToken(resetToken);
        user.setTokenExpiration(LocalDateTime.now().plusHours(1)); // Token valid for 1 hour
        userRepository.save(user);

        // Construct the password reset URL
        String resetUrl = passwordResetUrl+"?token=" + resetToken;

        // Prepare request body for email API
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("email", email);
        requestBody.put("resetLink", resetUrl);
        requestBody.put("currentYear", String.valueOf(LocalDateTime.now().getYear()));
        requestBody.put("username", user.getUsername());
        requestBody.put("validityTime", "60"); // 60 minutes validity time

        // Make HTTP call to email API using WebClient
        WebClient webClient = webClientBuilder.baseUrl(emailApiBaseUrl).build();

        String response = webClient.post()
                .uri("/forgot-password/link")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return response;
    }
    public void resetPassword(String token, String newPassword) throws Exception {
        // Find the user with the given reset token
        User user = userRepository.findByResetToken(token);
        if (user == null || user.getTokenExpiration().isBefore(LocalDateTime.now())) {
            throw new Exception("Invalid or expired token");
        }

        // Update the user's password
        user.setPassword(new BCryptPasswordEncoder().encode(newPassword));
        user.setResetToken(null); // Clear the token after a successful reset
        user.setTokenExpiration(null);
        userRepository.save(user);
    }

}
