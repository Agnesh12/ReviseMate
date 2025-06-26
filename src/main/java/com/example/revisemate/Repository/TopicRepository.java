package com.example.revisemate.Repository;

import com.example.revisemate.Model.Topic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// TopicRepository.java
public interface TopicRepository extends JpaRepository<Topic, Long> {
    List<Topic> findAllByUserIdOrderByCreatedAtDesc(Long userId);
}

