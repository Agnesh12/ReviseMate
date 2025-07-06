package com.example.revisemate.Controller;

import com.example.revisemate.DTO.JwtResponse;
import com.example.revisemate.DTO.TokenRefreshRequest;
import com.example.revisemate.Exception.TokenRefreshException;
import com.example.revisemate.Model.RefreshToken;
import com.example.revisemate.Model.User;
import com.example.revisemate.Repository.UserRepository;
import com.example.revisemate.Service.PasswordService;
import com.example.revisemate.Service.RefreshTokenService;
import com.example.revisemate.Util.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordService passwordService;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    public AuthController(UserRepository userRepository,
                          PasswordService passwordService,
                          JwtUtil jwtUtil,
                          RefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.passwordService = passwordService;
        this.jwtUtil = jwtUtil;
        this.refreshTokenService = refreshTokenService;
    }


    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody Map<String, String> payload) {

        String email = payload.get("email").toLowerCase().trim();
        String password = payload.get("password").trim();
        String name = payload.get("name").trim();

        if (userRepository.existsByEmailIgnoreCase(email)) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "User already exists with this email"));
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordService.hashPassword(password));
        user.setName(name);
        user.setCreatedAt(LocalDateTime.now());
        user = userRepository.save(user);

        String accessToken = jwtUtil.generateAccessToken(user.getId());
        String refreshToken = refreshTokenService
                .createRefreshToken(user.getId())
                .getToken();

        user.setPassword(null); // Do not return hashed password
        return ResponseEntity.status(201)
                .body(new JwtResponse(accessToken, refreshToken, user));
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> payload) {
        String email = payload.get("email").toLowerCase().trim();
        String password = payload.get("password").trim();

        Optional<User> userOpt = userRepository.findByEmailIgnoreCase(email);
        if (userOpt.isEmpty()) {
            System.out.println("LOGIN FAIL: User not found for " + email);
            return ResponseEntity.status(401)
                    .body(Map.of("error", "Invalid email or password"));
        }

        User user = userOpt.get();

        boolean matches = passwordService.comparePassword(password, user.getPassword());
        System.out.println("PASSWORD_MATCH? " + matches);

        if (!matches) {
            return ResponseEntity.status(401)
                    .body(Map.of("error", "Invalid email or password"));
        }

        String accessToken = jwtUtil.generateAccessToken(user.getId());
        String refreshToken = refreshTokenService
                .createRefreshToken(user.getId())
                .getToken();

        System.out.println(">>> Login successful, tokens issued for: " + user.getEmail());

        user.setPassword(null);  // Prevent hash leakage
        return ResponseEntity.ok(new JwtResponse(accessToken, refreshToken, user));
    }


    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody TokenRefreshRequest request) {

        String reqToken = request.getRefreshToken();

        return refreshTokenService.findByToken(reqToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String newAccess = jwtUtil.generateAccessToken(user.getId());
                    user.setPassword(null); // Don't return password
                    return ResponseEntity.ok(new JwtResponse(newAccess, reqToken, user));
                })
                .orElseThrow(() -> new TokenRefreshException("Refresh token not found"));
    }
}
