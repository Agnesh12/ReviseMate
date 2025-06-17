package com.example.revisemate.Service;


import com.example.revisemate.Model.Notification;
import com.example.revisemate.Model.Topic;
import com.example.revisemate.Model.User;
import com.example.revisemate.Repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;
    public void sendReminder(User user, Topic topic, long day) {
        String message = String.format("🔔 Reminder: It's been %d day(s) since you added topic \"%s\". Time to revise it!",day, topic.getTitle());
        Notification notification = new Notification(message, user);
        notificationRepository.save(notification);
    }
    public void markSeen(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(notification -> {
            notification.setSeen(true);
            notificationRepository.save(notification);
        });
    }

}
