package com.example.revisemate.Scheduler;


import com.example.revisemate.Model.Topic;
import com.example.revisemate.Model.User;
import com.example.revisemate.Repository.TopicRepository;
import com.example.revisemate.Service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class NotificationScheduler {

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private NotificationService notificationService;

    @Scheduled(cron = "0 0 8 * * ?") // Every day at 8 AM
    public void sendReminders() {
        LocalDate today = LocalDate.now();
        List<Topic> topics = topicRepository.findAll();
        System.out.println("🔔 Scheduler ran on " + today);

        for (Topic topic : topics) {
            User user = topic.getUser();
            if (user == null || user.getEmail() == null) continue;

            LocalDate createdDate = topic.getCreatedDate();
            long daysSinceAdded = ChronoUnit.DAYS.between(createdDate, today);

            int stage = topic.getNotificationStage();

            if (stage == 0 && daysSinceAdded >= 1) {
                notificationService.sendReminder(user, topic, 1);
                topic.setNotificationStage(1);
                topicRepository.save(topic);
            } else if (stage == 1 && daysSinceAdded >= 3) {
                notificationService.sendReminder(user, topic, 3);
                topic.setNotificationStage(2);
                topicRepository.save(topic);
            } else if (stage == 2 && daysSinceAdded >= 7) {
                notificationService.sendReminder(user, topic, 7);
                topic.setNotificationStage(3);
                topicRepository.save(topic);
            }
        }
    }

}
