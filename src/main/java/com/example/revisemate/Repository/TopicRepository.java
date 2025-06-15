package com.example.revisemate.Repository;

import com.example.revisemate.Model.Topic;
import com.example.revisemate.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TopicRepository extends JpaRepository<Topic, Long> {
    List<Topic> findByUser(User user);
}
