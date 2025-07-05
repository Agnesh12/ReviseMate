// src/main/java/com/example/revisemate/Controller/RevisionController.java
package com.example.revisemate.Controller;

import com.example.revisemate.Model.*; // Contains your Revision and Topic models
import com.example.revisemate.Repository.RevisionRepository;
import com.example.revisemate.Dto.RevisionDto; // <--- Import your DTO

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/revisions")
public class RevisionController {

    @Autowired private RevisionRepository revisionRepository;

    @GetMapping("/today")
    public ResponseEntity<List<RevisionDto>> getTodayRevisions(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        LocalDateTime todayStart = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime todayEnd = todayStart.plusDays(1).minusNanos(1);

        List<Revision> revisions = revisionRepository.findByDueDateBetweenAndCompletedAndTopic_User_Id(
                todayStart,
                todayEnd,
                0,
                userId
        );

        List<RevisionDto> revisionDtos = revisions.stream()
                .map(revision -> new RevisionDto(
                        revision.getId(),
                        // Null checks are good practice, though EntityGraph should reduce their necessity here
                        revision.getTopic() != null ? revision.getTopic().getId() : null,
                        revision.getTopic() != null ? revision.getTopic().getTitle() : "N/A",
                        revision.getTopic() != null ? revision.getTopic().getDescription() : "N/A",
                        revision.getRevisionNumber(),
                        revision.getDueDate(),
                        revision.getCompleted(),
                        revision.getCompletedAt(),
                        revision.getCreatedAt()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(revisionDtos);
    }

    @GetMapping
    public ResponseEntity<List<RevisionDto>> getAllRevisions(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");

        List<Revision> revisions = revisionRepository.findByTopic_User_Id(userId);

        List<RevisionDto> revisionDtos = revisions.stream()
                .map(revision -> new RevisionDto(
                        revision.getId(),
                        revision.getTopic() != null ? revision.getTopic().getId() : null,
                        revision.getTopic() != null ? revision.getTopic().getTitle() : "N/A",
                        revision.getTopic() != null ? revision.getTopic().getDescription() : "N/A",
                        revision.getRevisionNumber(),
                        revision.getDueDate(),
                        revision.getCompleted(),
                        revision.getCompletedAt(),
                        revision.getCreatedAt()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(revisionDtos);
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<?> markRevisionComplete(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");

        Revision revision = revisionRepository.findById(id).orElse(null);
        if (revision == null || revision.getTopic() == null || revision.getTopic().getUser() == null || !revision.getTopic().getUser().getId().equals(userId)) {
            return ResponseEntity.status(404).body(Map.of("error", "Revision not found or unauthorized"));
        }

        revision.setCompleted(1);
        revision.setCompletedAt(LocalDateTime.now());
        revisionRepository.save(revision);

        return ResponseEntity.ok(Map.of("success", true));
    }
}