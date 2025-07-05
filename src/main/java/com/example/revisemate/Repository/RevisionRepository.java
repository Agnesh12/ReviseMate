package com.example.revisemate.Repository;

import com.example.revisemate.Model.Revision;
import com.example.revisemate.Model.Topic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RevisionRepository extends JpaRepository<Revision, Long> {

    List<Revision> findByTopic(Topic topic);

    List<Revision> findByTopicId(Long topicId);

    List<Revision> findByDueDateBeforeAndCompleted(java.time.LocalDateTime date, int completed);

    List<Revision> findByCompleted(int completed);
}
