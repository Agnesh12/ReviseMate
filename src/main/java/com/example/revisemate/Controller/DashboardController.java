package com.example.revisemate.Controller;

import com.example.revisemate.Model.*;
import com.example.revisemate.Repository.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired private TopicRepository topicRepository;
    @Autowired private RevisionRepository revisionRepository;

    @GetMapping("/stats")
    public ResponseEntity<?> getStats(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");


        long totalTopics = topicRepository.countByUserId(userId);


        LocalDateTime todayStart = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime todayEnd = todayStart.plusDays(1).minusNanos(1);

        long todayRevisions = revisionRepository.countByDueDateBetweenAndCompletedAndTopic_User_Id(
                todayStart,
                todayEnd,
                0, // Not completed
                userId
        );


        long completedRevisions = revisionRepository.countByCompletedAndTopic_User_Id(
                1, // Completed
                userId
        );

        return ResponseEntity.ok(Map.of(
                "totalTopics", totalTopics,
                "todayRevisions", todayRevisions,
                "completedRevisions", completedRevisions
        ));
    }
}