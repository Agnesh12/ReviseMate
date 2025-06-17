package com.example.revisemate.Repository;

import com.example.revisemate.Model.Notification;
import com.example.revisemate.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserAndSeenFalse(User user);
}
