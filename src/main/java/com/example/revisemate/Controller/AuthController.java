package com.example.revisemate.Controller;

import com.example.revisemate.Model.AuthRequest;
import com.example.revisemate.Model.AuthResponse;
import com.example.revisemate.Model.RefreshToken; // <--- ENSURE THIS IS CORRECT
import com.example.revisemate.Model.RefreshTokenRequest;
import com.example.revisemate.Security.JwtService;
import com.example.revisemate.Service.AuthService;
import com.example.revisemate.Service.RefreshTokenService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    @Autowired
    private final AuthService authService;
    @Autowired
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;

    public AuthController(AuthService authService, JwtService jwtService, AuthenticationManager authenticationManager, RefreshTokenService refreshTokenService) {
        this.authService = authService;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> addNewUser(@Valid @RequestBody AuthRequest authRequest) {
        try {
            authService.saveUser(authRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
        } catch (IllegalArgumentException e) {
            logger.error("Registration error: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("An unexpected error occurred during registration: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest authRequest) {
        String normalizedUsername = authRequest.getUsername().trim().toLowerCase();
        logger.info("🟡 Login attempt: {} / {}", normalizedUsername, authRequest.getPassword());

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(normalizedUsername, authRequest.getPassword())
            );

            if (authentication.isAuthenticated()) {
                logger.info("✅ Authentication passed for user: {}", normalizedUsername);
                String accessToken = jwtService.generateToken(normalizedUsername);
                RefreshToken refreshToken = refreshTokenService.createRefreshToken(normalizedUsername);

                return ResponseEntity.ok(AuthResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken.getToken())
                        .message("Login successful")
                        .build());
            } else {
                logger.warn("❌ Authentication failed for user: {}", normalizedUsername);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(AuthResponse.builder()
                        .message("Authentication failed")
                        .build());
            }
        } catch (UsernameNotFoundException e) {
            logger.error("Authentication error: User not found - {}", normalizedUsername);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(AuthResponse.builder()
                    .message("Invalid username or password")
                    .build());
        } catch (Exception e) {
            logger.error("An unexpected error occurred during login for user {}: {}", normalizedUsername, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(AuthResponse.builder()
                    .message("An unexpected error occurred during login")
                    .build());
        }
    }

    @PostMapping("/refreshToken")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) { // <--- This line has the error, ensure RefreshTokenRequest is imported correctly
        logger.info("🔄 Refresh token request received: {}", refreshTokenRequest.getRefreshToken());
        try {
            return refreshTokenService.findByToken(refreshTokenRequest.getRefreshToken())
                    .map(refreshTokenService::verifyExpiration)
                    .map(RefreshToken::getUser)
                    .map(user -> {
                        String accessToken = jwtService.generateToken(user.getUsername());
                        logger.info("✅ Access token refreshed for user: {}", user.getUsername());
                        return ResponseEntity.ok(AuthResponse.builder()
                                .accessToken(accessToken)
                                .refreshToken(refreshTokenRequest.getRefreshToken())
                                .message("Token refreshed successfully")
                                .build());
                    }).orElseThrow(() -> {
                        logger.warn("❌ Refresh token is not in database: {}", refreshTokenRequest.getRefreshToken());
                        return new RuntimeException("Refresh token is not in database!");
                    });
        } catch (RuntimeException e) {
            logger.error("❌ Refresh token error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(AuthResponse.builder()
                    .message("Refresh token error: " + e.getMessage())
                    .build());
        } catch (Exception e) {
            logger.error("An unexpected error occurred during token refresh: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(AuthResponse.builder()
                    .message("An unexpected error occurred during token refresh")
                    .build());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        try {
            refreshTokenService.deleteToken(refreshTokenRequest.getRefreshToken());
            logger.info("🔴 User logged out successfully. Refresh token deleted: {}", refreshTokenRequest.getRefreshToken());
            return ResponseEntity.ok("User logged out successfully");
        } catch (Exception e) {
            logger.error("Error during logout: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error during logout: " + e.getMessage());
        }
    }
}