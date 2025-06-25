package com.example.revisemate.Controller;

import com.example.revisemate.Model.RevisionSchedule;
import com.example.revisemate.Model.Topic;
import com.example.revisemate.Model.User;
import com.example.revisemate.Repository.TopicRepository;
import com.example.revisemate.Repository.UserRepository;
import com.example.revisemate.Repository.RevisionScheduleRepository;
import com.example.revisemate.Security.JwtService;
import com.example.revisemate.exception.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional; // Ensure this import is present

@RestController
@RequestMapping("/user/topic")
public class TopicController {

    private final TopicRepository topicRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RevisionScheduleRepository revisionScheduleRepository;

    public TopicController(TopicRepository topicRepository,
                           UserRepository userRepository,
                           JwtService jwtService,
                           RevisionScheduleRepository revisionScheduleRepository) {
        this.topicRepository = topicRepository;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.revisionScheduleRepository = revisionScheduleRepository;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createTopic(@RequestBody Topic topic,
                                         HttpServletRequest request) {
        User user = getAuthenticatedUser(request);

        LocalDate today = LocalDate.now();
        LocalDate revisionDate3Days = today.plusDays(3);
        LocalDate revisionDate7Days = today.plusDays(7); // Correctly calculate 7 days from today

        // Create the topic with initial revision dates and notification stage 0
        Topic savedTopic = topicRepository.save(Topic.builder()
                .title(topic.getTitle())
                .description(topic.getDescription())
                .createdDate(today)
                .revisionDateDay3(revisionDate3Days) // Set the 3-day revision date
                .revisionDateDay7(revisionDate7Days) // Set the 7-day revision date here
                .notificationStage(0)
                .user(user)
                .build());

        // Create Day 3 revision schedule
        RevisionSchedule day3Schedule = RevisionSchedule.builder()
                .topic(savedTopic)
                .revisionDate(revisionDate3Days) // Use the calculated 3-day date
                .completed(false)
                .user(user)
                .build();

        // Create Day 7 revision schedule
        RevisionSchedule day7Schedule = RevisionSchedule.builder()
                .topic(savedTopic)
                .revisionDate(revisionDate7Days) // Use the calculated 7-day date
                .completed(false)
                .user(user)
                .build();

        // Save both revision schedules
        List<RevisionSchedule> savedSchedules = revisionScheduleRepository.saveAll(List.of(day3Schedule, day7Schedule));

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of(
                        "topic", savedTopic,
                        "revisionSchedules", savedSchedules
                ));
    }

    @GetMapping("/revisions")
    public ResponseEntity<List<RevisionSchedule>> getAllRevisions(HttpServletRequest request) {
        User user = getAuthenticatedUser(request);
        List<RevisionSchedule> revisions =
                revisionScheduleRepository.findByUserOrderByRevisionDateAsc(user);
        return ResponseEntity.ok(revisions);
    }

    @GetMapping // Changed to use the eager fetching method
    public ResponseEntity<List<Topic>> getAllTopics(HttpServletRequest request) {
        User user = getAuthenticatedUser(request);
        // Using the JOIN FETCH method from TopicRepository for better performance
        List<Topic> topics = topicRepository.findByUserWithRevisionSchedules(user);
        return ResponseEntity.ok(topics);
    }

    @GetMapping("/{id}") // Changed to use the eager fetching method
    public ResponseEntity<Topic> getTopic(@PathVariable long id, HttpServletRequest request) {
        User user = getAuthenticatedUser(request);
        // Using the JOIN FETCH method from TopicRepository for better performance
        return topicRepository.findByIdAndUserWithRevisionSchedules(id, user)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.FORBIDDEN).build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Topic> updateTopic(@PathVariable long id, @RequestBody Topic topic, HttpServletRequest request) {
        User user = getAuthenticatedUser(request);
        return topicRepository.findById(id)
                .filter(existing -> existing.getUser().getId() == user.getId())
                .map(existing -> {
                    existing.setTitle(topic.getTitle());
                    existing.setDescription(topic.getDescription());
                    // It's generally not recommended to update createdDate via PUT,
                    // but keeping it if that's your intended logic.
                    existing.setCreatedDate(topic.getCreatedDate());
                    existing.setRevisionDateDay3(topic.getRevisionDateDay3());
                    existing.setRevisionDateDay7(topic.getRevisionDateDay7());
                    Topic saved = topicRepository.save(existing);
                    return ResponseEntity.ok(saved);
                })
                .orElse(ResponseEntity.status(HttpStatus.FORBIDDEN).build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTopic(@PathVariable Long id, HttpServletRequest request) {
        User user = getAuthenticatedUser(request);
        return topicRepository.findById(id)
                .filter(existing -> existing.getUser().getId() == user.getId())
                .map(existing -> {
                    // Due to cascade = CascadeType.ALL and orphanRemoval = true in Topic.java,
                    // deleting the topic will automatically delete its associated RevisionSchedules.
                    topicRepository.delete(existing);
                    return new ResponseEntity<Void>(HttpStatus.NO_CONTENT); // 204 No Content
                })
                .orElse(ResponseEntity.status(HttpStatus.FORBIDDEN).build());
    }

    // Utility for authentication
    private User getAuthenticatedUser(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new UnauthorizedException("Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7);
        String username = jwtService.extractUsername(token);

        if (!jwtService.isTokenValid(token, username)) {
            throw new UnauthorizedException("Invalid or expired token");
        }

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UnauthorizedException("User not found"));
    }
}