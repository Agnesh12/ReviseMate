package com.example.revisemate.Controller;

import com.example.revisemate.Model.Notification;
import com.example.revisemate.Model.User;
import com.example.revisemate.Repository.NotificationRepository;
import com.example.revisemate.Repository.UserRepository;
import com.example.revisemate.Security.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @GetMapping("/unseen")
    public List<Notification> getUnseenNotifications(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        String username = jwtService.extractUsername(token);
        User user = userRepository.findByUsername(username).orElseThrow();

        LocalDate today = LocalDate.now();
        return notificationRepository.findByUserAndSeenFalseAndDueDate(user, today);
    }

    @PostMapping("/mark-as-seen/{id}")
    public void markAsSeen(HttpServletRequest request, @PathVariable Long id) {
        notificationRepository.findById(id).ifPresent(n -> {
            n.setSeen(true);
            notificationRepository.save(n);
        });
    }

    // Utility for authentication (can be moved to a shared component if many controllers use it)
    private User getAuthenticatedUser(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new UnauthorizedException("Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7);
        String username = jwtService.extractUsername(token);

        if (!jwtService.isTokenValid(token, username)) {
            throw new UnauthorizedException("Invalid or expired token");
        }

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UnauthorizedException("User not found"));
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    private static class UnauthorizedException extends RuntimeException {
        public UnauthorizedException(String message) {
            super(message);
        }
    }
}