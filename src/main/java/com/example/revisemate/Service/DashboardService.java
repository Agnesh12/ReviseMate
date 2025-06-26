package com.example.revisemate.Service;

import com.example.revisemate.DTO.DashboardDTO;
import com.example.revisemate.Repository.RevisionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

// DashboardService.java
@Service
@RequiredArgsConstructor
public class DashboardService {
    private final RevisionRepository revisionRepo;

    public DashboardDTO.DashboardStats stats(Long userId) {
        LocalDate today = LocalDate.now();
        return new DashboardDTO.DashboardStats(
                revisionRepo.countTopics(userId),
                revisionRepo.countTodayRevisions(userId, today),
                revisionRepo.countCompletedRevisions(userId)
        );
    }
}

