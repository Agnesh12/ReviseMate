package com.example.revisemate.Controller;

import com.example.revisemate.Model.*;
import com.example.revisemate.Repository.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/topics")
public class TopicController {

    @Autowired private TopicRepository topicRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private RevisionRepository revisionRepository;

    @PostMapping
    public ResponseEntity<?> createTopic(@RequestBody Topic topic, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        User user = userRepository.findById(userId).orElseThrow();

        topic.setUser(user);
        topic.setCreatedAt(LocalDateTime.now());
        Topic savedTopic = topicRepository.save(topic);


        int[] revisionIncrements = {1, 3, 7};


        LocalDateTime currentDueDate = LocalDateTime.now();

        for (int i = 0; i < revisionIncrements.length; i++) {
            int daysToAdd = revisionIncrements[i];


            currentDueDate = currentDueDate.plusDays(daysToAdd);

            Revision r = new Revision();
            r.setTopic(savedTopic);
            r.setRevisionNumber(i + 1);
            r.setDueDate(currentDueDate);
            r.setCompleted(0);
            r.setCreatedAt(LocalDateTime.now());
            revisionRepository.save(r);
        }

        return ResponseEntity.status(201).body(savedTopic);
    }

    @GetMapping
    public ResponseEntity<?> getTopics(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<Topic> topics = topicRepository.findByUserId(userId);
        return ResponseEntity.ok(topics);
    }
}