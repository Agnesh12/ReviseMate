package com.example.revisemate.Controller;

import com.example.revisemate.Model.RevisionSchedule;
import com.example.revisemate.Model.User;
import com.example.revisemate.Repository.RevisionScheduleRepository;
import com.example.revisemate.Repository.UserRepository;
import com.example.revisemate.Security.JwtService;
import com.example.revisemate.exception.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/user/revision")
public class RevisionScheduleController {

    private final RevisionScheduleRepository revisionScheduleRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public RevisionScheduleController(RevisionScheduleRepository revisionScheduleRepository,
                                      UserRepository userRepository,
                                      JwtService jwtService) {
        this.revisionScheduleRepository = revisionScheduleRepository;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    @GetMapping
    public ResponseEntity<List<RevisionSchedule>> getAllRevisions(HttpServletRequest request) {
        User user = getAuthenticatedUser(request);
        return ResponseEntity.ok(revisionScheduleRepository.findByUserOrderByRevisionDateAsc(user));
    }

    @GetMapping("/due")
    public ResponseEntity<List<RevisionSchedule>> getDueRevisions(HttpServletRequest request) {
        User user = getAuthenticatedUser(request);
        return ResponseEntity.ok(
                revisionScheduleRepository.findByUserAndRevisionDateLessThanEqualAndCompletedFalseOrderByRevisionDateAsc(
                        user, LocalDate.now()
                )
        );
    }

    @GetMapping("/upcoming")
    public ResponseEntity<List<RevisionSchedule>> getUpcomingRevisions(HttpServletRequest request) {
        User user = getAuthenticatedUser(request);
        return ResponseEntity.ok(
                revisionScheduleRepository.findByUserAndRevisionDateGreaterThanAndCompletedFalseOrderByRevisionDateAsc(
                        user, LocalDate.now()
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<RevisionSchedule> getRevision(@PathVariable Long id, HttpServletRequest request) {
        User user = getAuthenticatedUser(request);
        return revisionScheduleRepository.findById(id)
                .filter(rev -> rev.getUser().getId() == user.getId())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.FORBIDDEN).build());
    }

    @PostMapping
    public ResponseEntity<RevisionSchedule> createRevision(@RequestBody RevisionSchedule revisionSchedule,
                                                           HttpServletRequest request) {
        User user = getAuthenticatedUser(request);
        revisionSchedule.setUser(user);
        RevisionSchedule saved = revisionScheduleRepository.save(revisionSchedule);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RevisionSchedule> updateRevision(@PathVariable Long id,
                                                           @RequestBody RevisionSchedule revisionSchedule,
                                                           HttpServletRequest request) {
        User user = getAuthenticatedUser(request);
        return revisionScheduleRepository.findById(id)
                .filter(existing -> existing.getUser().getId() == user.getId())
                .map(existing -> {
                    existing.setRevisionDate(revisionSchedule.getRevisionDate());
                    existing.setCompleted(revisionSchedule.isCompleted());
                    return ResponseEntity.ok(revisionScheduleRepository.save(existing));
                })
                .orElse(ResponseEntity.status(HttpStatus.FORBIDDEN).build());
    }


    @PutMapping("/{id}/complete")
    public ResponseEntity<RevisionSchedule> markAsCompleted(@PathVariable Long id, HttpServletRequest request) {
        User user = getAuthenticatedUser(request);
        return revisionScheduleRepository.findById(id)
                .filter(rev -> rev.getUser().getId() == user.getId())
                .map(rev -> {
                    rev.setCompleted(true);
                    return ResponseEntity.ok(revisionScheduleRepository.save(rev));
                })
                .orElse(ResponseEntity.status(HttpStatus.FORBIDDEN).build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRevision(@PathVariable Long id, HttpServletRequest request) {
        User user = getAuthenticatedUser(request);
        return revisionScheduleRepository.findById(id)
                .filter(rev -> rev.getUser().getId() == user.getId())
                .map(rev -> {
                    revisionScheduleRepository.delete(rev); // Actual deletion
                    return new ResponseEntity<Void>(HttpStatus.NO_CONTENT); // 204 No Content
                })
                .orElse(ResponseEntity.status(HttpStatus.FORBIDDEN).build()); // Not found or forbidden
    }

    // Utility for authentication
    private User getAuthenticatedUser(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new UnauthorizedException("Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7);
        String username = jwtService.extractUsername(token);

        if (!jwtService.isTokenValid(token, username)) {
            throw new UnauthorizedException("Invalid or expired token");
        }

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UnauthorizedException("User not found"));
    }
}