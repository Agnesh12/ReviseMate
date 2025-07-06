package com.example.revisemate.Repository;

import com.example.revisemate.Model.Revision;
import com.example.revisemate.Model.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph; // Don't forget this import

import java.time.LocalDateTime;
import java.util.List;

public interface RevisionRepository extends JpaRepository<Revision, Long> {


    List<Revision> findByTopic(Topic topic);
    List<Revision> findByTopicId(Long topicId);
    List<Revision> findByDueDateBeforeAndCompleted(LocalDateTime date, int completed);
    List<Revision> findByCompleted(int completed);


    @EntityGraph(attributePaths = {"topic", "topic.user"})
    List<Revision> findByDueDateBetweenAndCompletedAndTopic_User_Id(LocalDateTime startOfDay, LocalDateTime endOfDay, int completed, Long userId);

    @EntityGraph(attributePaths = {"topic", "topic.user"})
    List<Revision> findByTopic_User_Id(Long userId);





    long countByDueDateBetweenAndCompletedAndTopic_User_Id(
            LocalDateTime startOfDay,
            LocalDateTime endOfDay,
            int completed,
            Long userId
    );


    long countByCompletedAndTopic_User_Id(int completed, Long userId);
}