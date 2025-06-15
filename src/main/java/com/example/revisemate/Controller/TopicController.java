package com.example.revisemate.Controller;


import com.example.revisemate.Model.Topic;
import com.example.revisemate.Model.User;
import com.example.revisemate.Repository.TopicRepository;
import com.example.revisemate.Repository.UserRepository;
import com.example.revisemate.Security.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/topic")
public class TopicController {

    private final  TopicRepository topicRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Autowired
    public TopicController(TopicRepository topicRepository, UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.topicRepository = topicRepository;
        this.jwtService = jwtService;
    }
    @GetMapping("{id}")
    public ResponseEntity<Topic> getTopic(@PathVariable long id) {
        return topicRepository.findById(id).map(topic -> new ResponseEntity<>(topic, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    @GetMapping
    public ResponseEntity<List<Topic>> getTopics() {
       List<Topic> topics = topicRepository.findAll();
       return new ResponseEntity<>(topics, HttpStatus.OK);
    }
    @PostMapping
    public ResponseEntity<Topic> createTopic(@RequestBody Topic topic, HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        String token = authHeader.substring(7); // Remove "Bearer "
        String username = jwtService.extractToken(token);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        topic.setUser(user);

        Topic savedTopic = topicRepository.save(topic);
        return new ResponseEntity<>(savedTopic, HttpStatus.CREATED);
    }

    @PutMapping("{id}")
    public ResponseEntity<Topic> updateTopic(@PathVariable long id, @RequestBody Topic topic) {
        return topicRepository.findById(id).map(oldTopic -> {
            oldTopic.setTitle(topic.getTitle());
            oldTopic.setDescription(topic.getDescription());
            oldTopic.setCreatedDate(topic.getCreatedDate());
            oldTopic.setRevisedDate(topic.getRevisedDate());
            return new ResponseEntity<>(topicRepository.save(oldTopic), HttpStatus.OK);
        }).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }


    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteTopic(@PathVariable long id) {
        if (!topicRepository.existsById(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        topicRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
