package com.example.revisemate.Scheduler;

import com.example.revisemate.Model.Notification;
import com.example.revisemate.Model.Topic;
import com.example.revisemate.Model.User;
import com.example.revisemate.Repository.NotificationRepository;
import com.example.revisemate.Repository.TopicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class NotificationScheduler {

    private final TopicRepository topicRepository;
    private final NotificationRepository notificationRepository;

    @Autowired
    public NotificationScheduler(TopicRepository topicRepository, NotificationRepository notificationRepository) {
        this.topicRepository = topicRepository;
        this.notificationRepository = notificationRepository;
    }

    // This method will run once every 24 hours (86400000 milliseconds)
    // You can adjust the fixedRate based on your needs.
    // Initial delay of 10 seconds to ensure application is fully started.
    @Scheduled(fixedRate = 86400000, initialDelay = 10000)
    @Transactional // Ensures atomicity of database operations
    public void generateRevisionNotifications() {
        LocalDate today = LocalDate.now();
        System.out.println("Running scheduled task to generate revision notifications for " + today);

        // --- Check for Day 3 Revisions ---
        // Find topics due for Day 3 revision where the Day 3 notification hasn't been sent yet (notificationStage == 0)
        List<Topic> day3DueTopics = topicRepository.findByRevisionDateDay3AndNotificationStage(today, 0);

        for (Topic topic : day3DueTopics) {
            User user = topic.getUser();
            String message = "Time to revise your topic: '" + topic.getTitle() + "' (Day 3)!";

            // Create notification for Day 3
            Notification newNotification = new Notification(message, user, today);
            notificationRepository.save(newNotification);

            // Update topic's notification stage to 1 (Day 3 notification sent)
            topic.setNotificationStage(1);
            topicRepository.save(topic);
            System.out.println("Generated Day 3 notification for topic: " + topic.getTitle() + " for user: " + user.getUsername());
        }

        // --- Check for Day 7 Revisions ---
        // Find topics due for Day 7 revision where the Day 3 notification has been sent (notificationStage == 1)
        // and Day 7 notification hasn't been sent yet
        List<Topic> day7DueTopics = topicRepository.findByRevisionDateDay7AndNotificationStage(today, 1);

        for (Topic topic : day7DueTopics) {
            User user = topic.getUser();
            String message = "Time to revise your topic: '" + topic.getTitle() + "' (Day 7)!";

            // Create notification for Day 7
            Notification newNotification = new Notification(message, user, today);
            notificationRepository.save(newNotification);

            // Update topic's notification stage to 2 (Day 7 notification sent)
            topic.setNotificationStage(2);
            topicRepository.save(topic);
            System.out.println("Generated Day 7 notification for topic: " + topic.getTitle() + " for user: " + user.getUsername());
        }
    }
}