package com.example.revisemate.Service;

import com.example.revisemate.DTO.RevisionDTO;
import com.example.revisemate.Model.Revision;
import com.example.revisemate.Model.Topic;
import com.example.revisemate.Repository.RevisionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;   // ✅ use Spring's runtime exception
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class RevisionService {

    private final RevisionRepository revisionRepo;

    public List<RevisionDTO.RevisionResponse> getTodayRevisions(Long userId) {
        return revisionRepo.dueToday(userId, LocalDate.now())
                .stream()
                .map(this::toDto)
                .toList();
    }

    public List<RevisionDTO.RevisionResponse> getAllRevisions(Long userId) {
        return revisionRepo.allForUser(userId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public void completeRevision(Long userId, Long revisionId) {
        Revision r = revisionRepo.findById(revisionId)
                .orElseThrow(() -> new NoSuchElementException("Not found"));

        if (!r.getTopic().getUser().getId().equals(userId)) {
            throw new AccessDeniedException("Forbidden");
        }

        r.setCompleted(true);
        r.setCompletedAt(LocalDateTime.now());
    }

    private RevisionDTO.RevisionResponse toDto(Revision r) {
        Topic t = r.getTopic();
        return new RevisionDTO.RevisionResponse(
                r.getId(),
                r.getRevisionNumber(),
                r.getDueDate(),
                r.isCompleted(),
                r.getCompletedAt(),
                t.getId(),
                t.getTitle(),
                t.getDescription());
    }
}
