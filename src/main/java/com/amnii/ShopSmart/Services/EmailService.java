package com.amnii.ShopSmart.Services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender emailSender;

    public void sendPasswordResetEmail(String to, String resetToken) {
        logger.info("Preparing to send password reset email to: {}", to);
        
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom("noreply@shopsmart.com");
            helper.setTo(to);
            helper.setSubject("ShopSmart - Password Reset Request");
            
            String resetLink = "http://localhost:3030/auth/reset-password?token=" + resetToken;
            
            String htmlContent = """
                <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px;">
                    <h2 style="color: #333;">Password Reset Request</h2>
                    <p>Hello,</p>
                    <p>You have requested to reset your password for your ShopSmart account.</p>
                    <p>Click the button below to reset your password:</p>
                    <div style="text-align: center; margin: 30px 0;">
                        <a href="%s" style="background-color: #4CAF50; color: white; padding: 12px 24px; text-decoration: none; border-radius: 4px; display: inline-block;">
                            Reset Password
                        </a>
                    </div>
                    <p>If you did not request a password reset, please ignore this email.</p>
                    <p><strong>Note:</strong> This link will expire in 15 minutes.</p>
                    <p>Best regards,<br>ShopSmart Team</p>
                </div>
                """.formatted(resetLink);
            
            helper.setText(htmlContent, true);
            
            logger.debug("Sending email to: {} with reset token: {}", to, resetToken);
            emailSender.send(message);
            logger.info("Successfully sent password reset email to: {}", to);
        } catch (MessagingException e) {
            logger.error("Failed to send password reset email to: {}. Error: {}", to, e.getMessage(), e);
            throw new RuntimeException("Failed to send password reset email: " + e.getMessage());
        }
    }
} 