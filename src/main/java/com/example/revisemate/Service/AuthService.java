package com.example.revisemate.Service;

import com.example.revisemate.DTO.AuthDTO;
import com.example.revisemate.Model.User;
import com.example.revisemate.Repository.UserRepository;
import com.example.revisemate.Security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

// AuthService.java
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;   // from your existing security config
    private final JwtService jwtService;             // you already created this

    public AuthDTO.AuthResponse signup(AuthDTO.SignupRequest req) {
        if (userRepo.existsByEmailIgnoreCase(req.email()))
            throw new IllegalArgumentException("User already exists");

        User user = new User();
        user.setEmail(req.email().toLowerCase());
        user.setPassword(passwordEncoder.encode(req.password()));
        user.setName(req.name());
        userRepo.save(user);
        return toAuthResponse(user);
    }

    public AuthDTO.AuthResponse login(AuthDTO.LoginRequest req) {
        User user = userRepo.findByEmailIgnoreCase(req.email())
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        if (!passwordEncoder.matches(req.password(), user.getPassword()))
            throw new IllegalArgumentException("Invalid credentials");

        return toAuthResponse(user);
    }

    private AuthDTO.AuthResponse toAuthResponse(User user) {
        String token = jwtService.generateToken(user.getId());
        return new AuthDTO.AuthResponse(user.getId(), user.getEmail(), user.getName(), token);
    }
}

