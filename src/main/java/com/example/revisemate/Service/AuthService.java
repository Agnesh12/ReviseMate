package com.example.revisemate.Service;

import com.example.revisemate.Model.AuthRequest; // Assuming AuthRequest is in Model
import com.example.revisemate.Model.User; // Assuming User is in Model
import com.example.revisemate.Repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Import this if you plan more complex ops

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void saveUser(AuthRequest authRequest) {

        if (userRepository.findByUsername(authRequest.getUsername().trim().toLowerCase()).isPresent()) {
            throw new IllegalArgumentException("Username already exists.");
        }
        if (userRepository.findByEmail(authRequest.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already registered.");
        }

        User newUser = User.builder()
                .username(authRequest.getUsername().trim().toLowerCase())
                .password(passwordEncoder.encode(authRequest.getPassword()))
                .email(authRequest.getEmail()) // Assuming AuthRequest has an email field
                .role("USER") // Default role
                .build();

        userRepository.save(newUser);
    }


}