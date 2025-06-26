package com.example.revisemate.DTO;

import java.time.LocalDateTime;

public class TopicDTO {
    // TopicDTOs.java
    public record TopicCreateRequest(String title, String description) {}
    public record TopicResponse(Long id, String title, String description, LocalDateTime createdAt) {}

}
