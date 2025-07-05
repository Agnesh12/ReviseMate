// src/main/java/com/example/revisemate/Dto/RevisionDto.java
package com.example.revisemate.Dto;

import java.time.LocalDateTime;

public class RevisionDto {
    private Long id;
    private Long topicId;
    private String topicTitle;    // <--- Key field for frontend
    private String topicDescription; // <--- Key field for frontend
    private int revisionNumber;
    private LocalDateTime dueDate;
    private boolean completed; // Using boolean in DTO is more Java-idiomatic for JSON
    private LocalDateTime completedAt;
    private LocalDateTime createdAt;

    // Constructor to map from Revision entity
    public RevisionDto(
            Long id, Long topicId, String topicTitle, String topicDescription,
            int revisionNumber, LocalDateTime dueDate, int completed, // int completed matches model
            LocalDateTime completedAt, LocalDateTime createdAt
    ) {
        this.id = id;
        this.topicId = topicId;
        this.topicTitle = topicTitle;
        this.topicDescription = topicDescription;
        this.revisionNumber = revisionNumber;
        this.dueDate = dueDate;
        this.completed = (completed == 1); // Convert int (0/1) to boolean
        this.completedAt = completedAt;
        this.createdAt = createdAt;
    }

    // Getters (needed for JSON serialization)
    public Long getId() { return id; }
    public Long getTopicId() { return topicId; }
    public String getTopicTitle() { return topicTitle; }
    public String getTopicDescription() { return topicDescription; }
    public int getRevisionNumber() { return revisionNumber; }
    public LocalDateTime getDueDate() { return dueDate; }
    public boolean isCompleted() { return completed; } // For boolean getters, it's 'is'
    public LocalDateTime getCompletedAt() { return completedAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}