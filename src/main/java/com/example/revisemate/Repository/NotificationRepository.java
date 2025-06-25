package com.example.revisemate.Repository;

import com.example.revisemate.Model.Notification;
import com.example.revisemate.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserAndSeenFalse(User user);
    List<Notification> findByUserAndSeenFalseAndDueDate(User user, LocalDate dueDate);
}