package com.amnii.ShopSmart.Controller;

import com.amnii.ShopSmart.DTO.*;
import com.amnii.ShopSmart.JwtService;
import com.amnii.ShopSmart.Repository.UserRepository;
import com.amnii.ShopSmart.Services.AuthService;
import com.amnii.ShopSmart.Services.ForgotPasswordService;
import com.amnii.ShopSmart.Services.CustomUserDetails;
import com.amnii.ShopSmart.Models.User;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;

@SuppressWarnings("unused")
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
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest request) {
        try {
            // Validate password match
            if (!request.getPassword().equals(request.getConfirmPassword())) {
                return ResponseEntity.badRequest()
                    .body(new ErrorResponse("VALIDATION_ERROR", "Passwords do not match"));
            }

            // Check if email already exists
            if (userRepo.findByEmail(request.getEmail()).isPresent()) {
                return ResponseEntity.badRequest()
                    .body(new ErrorResponse("VALIDATION_ERROR", "Email already registered"));
            }

            String message = authService.register(request);
            return ResponseEntity.ok(new AuthResponse(null, message));
        } catch (Exception e) {
            logger.error("Signup failed for email {}: {}", request.getEmail(), e.getMessage());
            return ResponseEntity.badRequest()
                .body(new ErrorResponse("SIGNUP_ERROR", "Failed to register user: " + e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            // Log the raw request data
            logger.info("Login request received: {}", request);

            // Validate request
            if (request == null) {
                logger.error("Login request is null");
                return ResponseEntity.badRequest()
                    .body(new ErrorResponse("VALIDATION_ERROR", "Request body is required"));
            }

            // Trim and validate email
            String email = request.getEmail() != null ? request.getEmail().trim() : "";
            if (email.isEmpty()) {
                logger.error("Login attempt with empty email");
                return ResponseEntity.badRequest()
                    .body(new ErrorResponse("VALIDATION_ERROR", "Email is required"));
            }

            // Validate password
            if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
                logger.error("Login attempt with empty password for email: {}", email);
                return ResponseEntity.badRequest()
                    .body(new ErrorResponse("VALIDATION_ERROR", "Password is required"));
            }

            logger.info("Attempting login for email: [{}]", email);
            
            try {
                // Attempt authentication
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(email, request.getPassword())
                );

                // Find user and generate token
                User user = userRepo.findByEmail(email)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found"));

                String token = jwtService.generateToken(user);
                logger.info("Login successful for user: [{}]", email);

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
            } catch (BadCredentialsException e) {
                logger.error("Invalid credentials for user [{}]: {}", email, e.getMessage());
                return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED)
                        .body(new ErrorResponse("AUTHENTICATION_ERROR", "Invalid email or password"));
            }
        } catch (Exception e) {
            logger.error("Login failed: {}", e.getMessage());
            return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("SERVER_ERROR", "An error occurred during login"));
        }
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

    @PutMapping("/update-profile")
    public ResponseEntity<?> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        try {
            // Get the current authenticated user
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            logger.info("Updating profile for user: {}", email);

            // Find the user
            User user = userRepo.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            // Update user fields
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            if (request.getShopName() != null) {
                user.setShopName(request.getShopName());
            }
            if (request.getAddress() != null) {
                user.setAddress(request.getAddress());
            }

            // Save the updated user
            userRepo.save(user);
            logger.info("Profile updated successfully for user: {}", email);

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
        } catch (UsernameNotFoundException e) {
            logger.error("User not found during profile update: {}", e.getMessage());
            return ResponseEntity.status(HttpServletResponse.SC_NOT_FOUND)
                    .body(new ErrorResponse("USER_NOT_FOUND", "User not found"));
        } catch (Exception e) {
            logger.error("Error updating profile: {}", e.getMessage());
            return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("UPDATE_ERROR", "Failed to update profile"));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        try {
            // Get the current authenticated user
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            logger.info("Getting current user data for: {}", email);

            // Find the user
            User user = userRepo.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            // Create user DTO
            UserDTO userDTO = new UserDTO(
                String.valueOf(user.getId()),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole(),
                user.getShopName(),
                user.getAddress()
            );

            return ResponseEntity.ok(userDTO);
        } catch (UsernameNotFoundException e) {
            logger.error("User not found: {}", e.getMessage());
            return ResponseEntity.status(HttpServletResponse.SC_NOT_FOUND)
                    .body(new ErrorResponse("USER_NOT_FOUND", "User not found"));
        } catch (Exception e) {
            logger.error("Error getting current user: {}", e.getMessage());
            return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("SERVER_ERROR", "Failed to get user data"));
        }
    }
}
