package com.amnii.ShopSmart.Services;

import com.amnii.ShopSmart.Models.User;
import com.amnii.ShopSmart.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ForgotPasswordService {
    private static final Logger logger = LoggerFactory.getLogger(ForgotPasswordService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void processForgotPassword(String email) {
        logger.info("Processing forgot password request for email: {}", email);
        
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.error("User not found with email: {}", email);
                    return new RuntimeException("User not found with email: " + email);
                });

        // Generate a random token
        String token = UUID.randomUUID().toString();
        logger.debug("Generated reset token for email {}: {}", email, token);

        // Store the token in the database with an expiration time (15 minutes from now)
        user.setResetToken(token);
        user.setResetTokenExpiry(LocalDateTime.now().plusMinutes(15));
        userRepository.save(user);
        logger.info("Saved reset token for user: {}", email);

        // Send the reset email
        try {
            emailService.sendPasswordResetEmail(email, token);
            logger.info("Successfully sent reset email to: {}", email);
        } catch (Exception e) {
            logger.error("Failed to send reset email to: {}. Error: {}", email, e.getMessage(), e);
            throw new RuntimeException("Failed to send reset email: " + e.getMessage());
        }
    }

    public boolean validateResetToken(String token) {
        logger.info("Validating reset token: {}", token);
        
        User user = userRepository.findByResetToken(token)
                .orElseThrow(() -> {
                    logger.error("Invalid reset token: {}", token);
                    return new RuntimeException("Invalid password reset token");
                });

        // Check if token has expired
        if (user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            logger.error("Reset token has expired for token: {}", token);
            throw new RuntimeException("Password reset token has expired");
        }

        logger.info("Token validation successful for token: {}", token);
        return true;
    }

    public void resetPassword(String token, String newPassword) {
        logger.info("Processing password reset for token: {}", token);
        
        User user = userRepository.findByResetToken(token)
                .orElseThrow(() -> {
                    logger.error("Invalid reset token: {}", token);
                    return new RuntimeException("Invalid password reset token");
                });

        // Check if token has expired
        if (user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            logger.error("Reset token has expired for token: {}", token);
            throw new RuntimeException("Password reset token has expired");
        }

        // Update password and clear reset token
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);
        logger.info("Successfully reset password for user: {}", user.getEmail());
    }
} 