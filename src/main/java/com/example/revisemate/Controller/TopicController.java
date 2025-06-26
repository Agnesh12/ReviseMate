package com.example.revisemate.Controller;

import com.example.revisemate.DTO.TopicDTO;
import com.example.revisemate.Service.TopicService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/topics") @RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")   // if you’re using springdoc-openapi
public class TopicController {
    private final TopicService topicService;

    @PostMapping
    public TopicDTO.TopicResponse create(@AuthenticationPrincipal Long userId,
                                         @Valid @RequestBody TopicDTO.TopicCreateRequest req) {
        return topicService.createTopic(userId, req);
    }

    @GetMapping
    public List<TopicDTO.TopicResponse> list(@AuthenticationPrincipal Long userId) {
        return topicService.listTopics(userId);
    }
}

