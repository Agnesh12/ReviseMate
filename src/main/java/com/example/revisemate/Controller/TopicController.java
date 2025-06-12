package com.example.revisemate.Controller;


import com.example.revisemate.Model.Topic;
import com.example.revisemate.Repository.TopicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/topic")
public class TopicController {

    private final  TopicRepository topicRepository;


    @Autowired
    public TopicController(TopicRepository topicRepository) {
        this.topicRepository = topicRepository;
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
    public ResponseEntity<Topic> createTopic(@RequestBody Topic topic) {
        Topic  newTopic = topicRepository.save(topic);
        return new ResponseEntity<>(newTopic, HttpStatus.CREATED);
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
