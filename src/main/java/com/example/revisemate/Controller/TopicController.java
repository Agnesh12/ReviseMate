package com.example.revisemate.Controller;

import com.example.revisemate.Model.Topic;
import com.example.revisemate.Model.User;
import com.example.revisemate.Repository.TopicRepository;
import com.example.revisemate.Repository.UserRepository;
import com.example.revisemate.Security.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/topic")
public class TopicController {

    private final TopicRepository topicRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public TopicController(TopicRepository topicRepository, UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.topicRepository = topicRepository;
        this.jwtService = jwtService;
    }

    @GetMapping("{id}")
    public ResponseEntity<Topic> getTopic(@PathVariable long id, HttpServletRequest request) {
        User user = getAuthenticatedUser(request);

        var optionalTopic = topicRepository.findById(id);

        if (optionalTopic.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Topic topic = optionalTopic.get();

        if (!topic.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(topic);
    }


    @GetMapping
    public ResponseEntity<List<Topic>> getTopics(HttpServletRequest request) {
        User user = getAuthenticatedUser(request);
        List<Topic> topics = topicRepository.findByUser(user);
        return new ResponseEntity<>(topics, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Topic> createTopic(@RequestBody Topic topic, HttpServletRequest request) {
        User user = getAuthenticatedUser(request);
        topic.setUser(user);
        Topic savedTopic = topicRepository.save(topic);
        return new ResponseEntity<>(savedTopic, HttpStatus.CREATED);
    }

    @PutMapping("{id}")
    public ResponseEntity<Topic> updateTopic(@PathVariable long id, @RequestBody Topic topic, HttpServletRequest request) {
        User user = getAuthenticatedUser(request);

        var optionalTopic = topicRepository.findById(id);

        if (optionalTopic.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Topic existingTopic = optionalTopic.get();

        if (!existingTopic.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        existingTopic.setTitle(topic.getTitle());
        existingTopic.setDescription(topic.getDescription());
        existingTopic.setCreatedDate(topic.getCreatedDate());
        existingTopic.setRevisedDate(topic.getRevisedDate());

        Topic savedTopic = topicRepository.save(existingTopic);
        return ResponseEntity.ok(savedTopic);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteTopic(@PathVariable long id, HttpServletRequest request) {
        User user = getAuthenticatedUser(request);

        var optionalTopic = topicRepository.findById(id);
        if (optionalTopic.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Topic topic = optionalTopic.get();
        if (!topic.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        topicRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

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

@ResponseStatus(HttpStatus.UNAUTHORIZED)
class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}
