package com.amnii.ShopSmart.Controller;

import com.amnii.ShopSmart.DTO.*;
import com.amnii.ShopSmart.JwtService;
import com.amnii.ShopSmart.Repository.UserRepository;
import com.amnii.ShopSmart.Services.AuthService;
import com.amnii.ShopSmart.Models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;


@RestController
@CrossOrigin(origins = "http://localhost:3030")
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private AuthService authService;

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
                user.getRole()
        );

        // Build response object
        LoginResponse response = new LoginResponse(token, userDTO, "Login successful");

        return ResponseEntity.ok(response);
    }

}
