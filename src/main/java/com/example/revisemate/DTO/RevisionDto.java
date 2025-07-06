
package com.example.revisemate.Dto;

import java.time.LocalDateTime;

public class RevisionDto {
    private Long id;
    private Long topicId;
    private String topicTitle;
    private String topicDescription;
    private int revisionNumber;
    private LocalDateTime dueDate;
    private boolean completed;
    private LocalDateTime completedAt;
    private LocalDateTime createdAt;


    public RevisionDto(
            Long id, Long topicId, String topicTitle, String topicDescription,
            int revisionNumber, LocalDateTime dueDate, int completed,
            LocalDateTime completedAt, LocalDateTime createdAt
    ) {
        this.id = id;
        this.topicId = topicId;
        this.topicTitle = topicTitle;
        this.topicDescription = topicDescription;
        this.revisionNumber = revisionNumber;
        this.dueDate = dueDate;
        this.completed = (completed == 1);
        this.completedAt = completedAt;
        this.createdAt = createdAt;
    }


    public Long getId() { return id; }
    public Long getTopicId() { return topicId; }
    public String getTopicTitle() { return topicTitle; }
    public String getTopicDescription() { return topicDescription; }
    public int getRevisionNumber() { return revisionNumber; }
    public LocalDateTime getDueDate() { return dueDate; }
    public boolean isCompleted() { return completed; }
    public LocalDateTime getCompletedAt() { return completedAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}