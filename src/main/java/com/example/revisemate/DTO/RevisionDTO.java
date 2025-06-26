package com.example.revisemate.DTO;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class RevisionDTO {
    // RevisionDTOs.java
    public record RevisionResponse(
            Long id,
            Integer revisionNumber,
            LocalDate dueDate,
            boolean completed,
            LocalDateTime completedAt,
            Long topicId,
            String title,
            String description) {}

}
