package com.example.revisemate.Controller;

import com.example.revisemate.DTO.AuthDTO;
import com.example.revisemate.Service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth") @RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup")
    public AuthDTO.AuthResponse signup(@Valid @RequestBody AuthDTO.SignupRequest req) {
        return authService.signup(req);
    }

    @PostMapping("/login")
    public AuthDTO.AuthResponse login(@Valid @RequestBody AuthDTO.LoginRequest req) {
        return authService.login(req);
    }
}
