package com.in.auth.service;


import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.in.auth.repository.UserRepository;
import com.in.security.models.User;

@Service
public class PasswordResetService {

    // Ideally, you would inject your email service and user repository here
	@Autowired
    private EmailService emailService;
    @Autowired
    private UserRepository userRepository;

	/*
	 * public PasswordResetService(EmailService emailService, UserRepository
	 * userRepository) { this.emailService = emailService; this.userRepository =
	 * userRepository; }
	 */

    public String initiatePasswordReset(String email) throws Exception {
        // Step 1: Verify if the email exists in the database
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new Exception("User with this email not found");
        }

        // Step 2: Generate a password reset token
        String resetToken = UUID.randomUUID().toString();

        // Step 3: Save the reset token and its expiration to the user's record (in a real app)
        user.setResetToken(resetToken);
        user.setTokenExpiration(LocalDateTime.now().plusHours(1)); // Token valid for 1 hour
        userRepository.save(user);

        // Step 4: Construct the password reset URL
        String resetUrl = "https://139.59.67.255/identitymanagement/api/auth/reset-password?token=" + resetToken;

        // Step 5: Send the reset link to the user's email
        String emailSubject = "Password Reset Request";
        String emailBody = "To reset your password, click the link below:\n" + resetUrl;
        emailService.sendEmail(email, emailSubject, emailBody);
        
        return emailBody;
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
