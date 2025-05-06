package com.amnii.ShopSmart.Controller;

import com.amnii.ShopSmart.DTO.*;
import com.amnii.ShopSmart.JwtService;
import com.amnii.ShopSmart.Repository.UserRepository;
import com.amnii.ShopSmart.Services.AuthService;
import com.amnii.ShopSmart.Services.ForgotPasswordService;
import com.amnii.ShopSmart.Services.CustomUserDetails;
import com.amnii.ShopSmart.Models.User;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;

@RestController
@CrossOrigin(origins = "http://localhost:3030")
@RequestMapping("/api/auth")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private AuthService authService;

    @Autowired
    private ForgotPasswordService forgotPasswordService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest request) {
        String message = authService.register(request);
        return ResponseEntity.ok(new AuthResponse(null, message));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String token = jwtService.generateToken(user);

        // Build user DTO for response
        UserDTO userDTO = new UserDTO(
                String.valueOf(user.getId()),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole(),
                user.getShopName(),
                user.getAddress()
        );

        // Build response object
        LoginResponse response = new LoginResponse(token, userDTO, "Login successful");

        return ResponseEntity.ok(response);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        logger.info("Received forgot password request for email: {}", request.getEmail());
        try {
            forgotPasswordService.processForgotPassword(request.getEmail());
            logger.info("Successfully processed forgot password request for email: {}", request.getEmail());
            return ResponseEntity.ok(new AuthResponse(null, "Password reset email sent successfully"));
        } catch (Exception e) {
            logger.error("Error processing forgot password request for email: {}. Error: {}", 
                request.getEmail(), e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ErrorResponse("FORGOT_PASSWORD_ERROR", e.getMessage()));
        }
    }

    @GetMapping("/validate-reset-token")
    public ResponseEntity<?> validateResetToken(@RequestParam String token) {
        logger.info("Validating reset token: {}", token);
        try {
            forgotPasswordService.validateResetToken(token);
            logger.info("Token validation successful for token: {}", token);
            return ResponseEntity.ok(new AuthResponse(null, "Token is valid"));
        } catch (Exception e) {
            logger.error("Token validation failed for token: {}. Error: {}", token, e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ErrorResponse("TOKEN_VALIDATION_ERROR", e.getMessage()));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        logger.info("Received password reset request for token: {}", request.getToken());
        try {
            forgotPasswordService.resetPassword(request.getToken(), request.getNewPassword());
            logger.info("Successfully reset password for token: {}", request.getToken());
            return ResponseEntity.ok(new AuthResponse(null, "Password has been reset successfully"));
        } catch (Exception e) {
            logger.error("Error resetting password for token: {}. Error: {}", 
                request.getToken(), e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ErrorResponse("PASSWORD_RESET_ERROR", e.getMessage()));
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        logger.info("Updating user profile");
        try {
            User user = userRepo.findByEmail(
                ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername()
            ).orElseThrow(() -> new UsernameNotFoundException("User not found"));

            // Update user fields
            if (request.getFirstName() != null) user.setFirstName(request.getFirstName());
            if (request.getLastName() != null) user.setLastName(request.getLastName());
            if (request.getShopName() != null) user.setShopName(request.getShopName());
            if (request.getAddress() != null) user.setAddress(request.getAddress());

            userRepo.save(user);

            // Create updated user DTO
            UserDTO userDTO = new UserDTO(
                String.valueOf(user.getId()),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole(),
                user.getShopName(),
                user.getAddress()
            );

            return ResponseEntity.ok(new LoginResponse(null, userDTO, "Profile updated successfully"));
        } catch (Exception e) {
            logger.error("Error updating profile: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ErrorResponse("PROFILE_UPDATE_ERROR", e.getMessage()));
        }
    }
}
