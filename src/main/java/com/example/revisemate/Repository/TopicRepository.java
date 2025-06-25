// src/main/java/com/example/revisemate/Repository/TopicRepository.java
package com.example.revisemate.Repository;

import com.example.revisemate.Model.Topic;
import com.example.revisemate.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; // Make sure this is imported
import org.springframework.data.repository.query.Param; // Make sure this is imported

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TopicRepository extends JpaRepository<Topic, Long> {

    List<Topic> findByUser(User user);

    @Query("SELECT t FROM Topic t JOIN FETCH t.revisionSchedules WHERE t.user = :user ORDER BY t.createdDate DESC")
    List<Topic> findByUserWithRevisionSchedules(@Param("user") User user);

    @Query("SELECT t FROM Topic t JOIN FETCH t.revisionSchedules WHERE t.id = :id AND t.user = :user")
    Optional<Topic> findByIdAndUserWithRevisionSchedules(@Param("id") Long id, @Param("user") User user);

    // --- NEW METHODS FOR SCHEDULER ---
    List<Topic> findByRevisionDateDay3AndNotificationStage(LocalDate revisionDateDay3, int notificationStage);

    List<Topic> findByRevisionDateDay7AndNotificationStage(LocalDate revisionDateDay7, int notificationStage);
    // --- END NEW METHODS ---
}