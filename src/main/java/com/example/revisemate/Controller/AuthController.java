package com.example.revisemate.Controller;


import com.example.revisemate.Model.AuthRequest;
import com.example.revisemate.Model.AuthResponse;
import com.example.revisemate.Model.User;
import com.example.revisemate.Repository.UserRepository;
import com.example.revisemate.Security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/")
public class AuthController {

    @Autowired
    private  UserRepository userRepository;

    @Autowired
    private  JwtService jwtService;

    @Autowired
    private  AuthenticationManager authenticationManager;
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody AuthRequest request) {

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists.");
        }

        String encodedPassword = new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode(request.getPassword());
        User user = new User(request.getUsername(), encodedPassword, "USER", request.getEmail());
        userRepository.save(user);

        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        if (authentication.isAuthenticated()) {
            User user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            String token = jwtService.generateToken(user.getUsername());
            return ResponseEntity.ok(new AuthResponse(token));
        } else {
            throw new RuntimeException("Invalid login credentials");
        }
    }


}
