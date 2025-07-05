package com.example.revisemate.Controller;

import com.example.revisemate.Model.*;
import com.example.revisemate.Repository.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired private TopicRepository topicRepository;
    @Autowired private RevisionRepository revisionRepository;

    @GetMapping("/stats")
    public ResponseEntity<?> getStats(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        LocalDateTime today = LocalDateTime.now();

        long totalTopics = topicRepository.findByUserId(userId).size();

        long todayRevisions = revisionRepository.findByDueDateBeforeAndCompleted(today, 0).stream()
                .filter(r -> r.getTopic().getUser().getId().equals(userId)).count();

        long completedRevisions = revisionRepository.findByCompleted(1).stream()
                .filter(r -> r.getTopic().getUser().getId().equals(userId)).count();

        return ResponseEntity.ok(Map.of(
                "totalTopics", totalTopics,
                "todayRevisions", todayRevisions,
                "completedRevisions", completedRevisions
        ));
    }
}
