package com.example.revisemate.DTO;

public class AuthDTO {
    // AuthDTOs.java
    public record SignupRequest(String email, String password, String name) {}
    public record LoginRequest(String email, String password) {}
    public record AuthResponse(Long id, String email, String name, String token) {}

}
