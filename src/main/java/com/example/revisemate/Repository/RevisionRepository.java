package com.example.revisemate.Repository;

import com.example.revisemate.Model.Revision;
import com.example.revisemate.Model.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph; // Don't forget this import

import java.time.LocalDateTime;
import java.util.List;

public interface RevisionRepository extends JpaRepository<Revision, Long> {

    // Existing methods (keep them if used elsewhere)
    List<Revision> findByTopic(Topic topic);
    List<Revision> findByTopicId(Long topicId);
    List<Revision> findByDueDateBeforeAndCompleted(LocalDateTime date, int completed); // Keep if still used, but consider replacing with ByDueDateBetween for "today"
    List<Revision> findByCompleted(int completed); // Keep if still used

    // Methods for RevisionController (already discussed and should be there):
    @EntityGraph(attributePaths = {"topic", "topic.user"})
    List<Revision> findByDueDateBetweenAndCompletedAndTopic_User_Id(LocalDateTime startOfDay, LocalDateTime endOfDay, int completed, Long userId);

    @EntityGraph(attributePaths = {"topic", "topic.user"})
    List<Revision> findByTopic_User_Id(Long userId);


    // NEW METHODS FOR DASHBOARDCONTROLLER:

    // Count revisions due today for a specific user (avoids stream filtering in Java)
    long countByDueDateBetweenAndCompletedAndTopic_User_Id(
            LocalDateTime startOfDay,
            LocalDateTime endOfDay,
            int completed,
            Long userId
    );

    // Count completed revisions for a specific user (avoids stream filtering in Java)
    long countByCompletedAndTopic_User_Id(int completed, Long userId);
}