package com.example.revisemate.Service;

import com.example.revisemate.Model.Notification;
import com.example.revisemate.Model.Topic;
import com.example.revisemate.Model.User;
import com.example.revisemate.Repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    public void sendReminder(User user, Topic topic, int day) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setMessage("⏰ " + day + "-Day Revision: " + topic.getTitle());
        notification.setSeen(false);
        notification.setDueDate(LocalDate.now()); // ✅ very important
        notification.setCreatedAt(LocalDateTime.now());

        notificationRepository.save(notification);
    }
}
