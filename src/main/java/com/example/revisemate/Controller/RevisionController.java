package com.example.revisemate.Controller;

import com.example.revisemate.DTO.RevisionDTO;
import com.example.revisemate.Service.RevisionService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/revisions") @RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class RevisionController {
    private final RevisionService revisionService;

    @GetMapping("/today")
    public List<RevisionDTO.RevisionResponse> today(@AuthenticationPrincipal Long userId) {
        return revisionService.getTodayRevisions(userId);
    }

    @GetMapping
    public List<RevisionDTO.RevisionResponse> all(@AuthenticationPrincipal Long userId) {
        return revisionService.getAllRevisions(userId);
    }

    @PatchMapping("/{id}/complete")
    public void complete(@AuthenticationPrincipal Long userId,
                         @PathVariable Long id) {
        revisionService.completeRevision(userId, id);
    }
}

