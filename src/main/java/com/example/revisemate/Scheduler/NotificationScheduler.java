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

    @Scheduled(cron = "0 0 8 * * ?")
    public void sendRemainders() {
        LocalDate today = LocalDate.now();
        List<Topic> topics = topicRepository.findAll();
        for(Topic topic : topics) {
            User user = topic.getUser();
            LocalDate createdDate = topic.getCreatedDate();
           long daysSinceAdded = ChronoUnit.DAYS.between(createdDate, today);
            if(topic.getNotificationStage() == 0 && daysSinceAdded >= 3) {
                topic.setNotificationStage(1);
                notificationService.sendReminder(user, topic, 3);
                topicRepository.save(topic);
            }
            else if(topic.getNotificationStage() == 1 && daysSinceAdded >= 10) {
                notificationService.sendReminder(user, topic, 10);
                topic.setNotificationStage(2);
                topicRepository.save(topic);
            }
        }
    }
}
