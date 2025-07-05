package com.example.revisemate.Controller;

import com.example.revisemate.Model.*;
import com.example.revisemate.Repository.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/revisions")
public class RevisionController {

    @Autowired private RevisionRepository revisionRepository;
    @Autowired private TopicRepository topicRepository;

    @GetMapping("/today")
    public ResponseEntity<?> getTodayRevisions(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        LocalDateTime today = LocalDateTime.now();

        List<Revision> revisions = revisionRepository.findByDueDateBeforeAndCompleted(today, 0);
        List<Revision> filtered = revisions.stream()
                .filter(r -> r.getTopic().getUser().getId().equals(userId))
                .toList();

        return ResponseEntity.ok(filtered);
    }

    @GetMapping
    public ResponseEntity<?> getAllRevisions(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");

        List<Revision> revisions = revisionRepository.findAll().stream()
                .filter(r -> r.getTopic().getUser().getId().equals(userId))
                .toList();

        return ResponseEntity.ok(revisions);
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<?> markRevisionComplete(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");

        Revision revision = revisionRepository.findById(id).orElse(null);
        if (revision == null || !revision.getTopic().getUser().getId().equals(userId)) {
            return ResponseEntity.status(404).body(Map.of("error", "Revision not found"));
        }

        revision.setCompleted(1);
        revision.setCompletedAt(LocalDateTime.now());
        revisionRepository.save(revision);

        return ResponseEntity.ok(Map.of("success", true));
    }
}
