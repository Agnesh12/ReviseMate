package com.example.revisemate.Controller;


import com.example.revisemate.Model.Notification;
import com.example.revisemate.Model.User;
import com.example.revisemate.Repository.NotificationRepository;
import com.example.revisemate.Repository.UserRepository;
import com.example.revisemate.Security.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
        return notificationRepository.findByUserAndSeenFalse(user);
    }


    @PostMapping("/mark-as-seen/{id}")
    public void markAsSeen(HttpServletRequest request, @PathVariable Long id) {
        notificationRepository.findById(id).ifPresent(n -> {
            n.setSeen(true);
            notificationRepository.save(n);
        });
    }

}
