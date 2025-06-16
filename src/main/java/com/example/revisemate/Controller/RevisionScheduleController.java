package com.example.revisemate.Controller;

import com.example.revisemate.Model.RevisionSchedule;
import com.example.revisemate.Model.User;
import com.example.revisemate.Repository.RevisionScheduleRepository;
import com.example.revisemate.Repository.UserRepository;
import com.example.revisemate.Security.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/revision")
public class RevisionScheduleController {

    private final UserRepository userRepository;
    private final RevisionScheduleRepository revisionScheduleRepository;
    private final JwtService jwtService;

    @Autowired
    public RevisionScheduleController(UserRepository userRepository, RevisionScheduleRepository revisionScheduleRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.revisionScheduleRepository = revisionScheduleRepository;
        this.jwtService = jwtService;
    }

    @GetMapping
    public ResponseEntity<List<RevisionSchedule>> getRevisions(HttpServletRequest request) {
        User user = getAuthenticatedUser(request);
        List<RevisionSchedule> revisions = revisionScheduleRepository.findAll()
                .stream()
                .filter(rev -> rev.getUser().getId().equals(user.getId()))
                .toList();
        return ResponseEntity.ok(revisions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RevisionSchedule> getRevision(HttpServletRequest request, @PathVariable Long id) {
        User user = getAuthenticatedUser(request);
        return revisionScheduleRepository.findById(id)
                .filter(rev -> rev.getUser().getId().equals(user.getId()))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.FORBIDDEN).build());
    }

    @PostMapping
    public ResponseEntity<RevisionSchedule> createRevision(@RequestBody RevisionSchedule revisionSchedule, HttpServletRequest request) {
        User user = getAuthenticatedUser(request);
        revisionSchedule.setUser(user);
        RevisionSchedule saved = revisionScheduleRepository.save(revisionSchedule);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RevisionSchedule> updateRevision(@RequestBody RevisionSchedule revisionSchedule, HttpServletRequest request, @PathVariable Long id) {
        User user = getAuthenticatedUser(request);
        return revisionScheduleRepository.findById(id)
                .filter(existing -> existing.getUser().getId().equals(user.getId()))
                .map(existing -> {
                    existing.setRevisionDate(revisionSchedule.getRevisionDate());
                    existing.setCompleted(revisionSchedule.isCompleted());
                    return ResponseEntity.ok(revisionScheduleRepository.save(existing));
                })
                .orElse(ResponseEntity.status(HttpStatus.FORBIDDEN).build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRevision(@PathVariable Long id, HttpServletRequest request) {
        User user = getAuthenticatedUser(request);
        return revisionScheduleRepository.findById(id)
                .filter(rev -> rev.getUser().getId().equals(user.getId()))
                .map(rev -> {
                    revisionScheduleRepository.delete(rev);
                    return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
                })
                .orElse(ResponseEntity.status(HttpStatus.FORBIDDEN).build());
    }
    @PutMapping("/{id}/complete")
    public ResponseEntity<RevisionSchedule> markAsCompleted(@PathVariable Long id, HttpServletRequest request) {
        User user = getAuthenticatedUser(request);

        return revisionScheduleRepository.findById(id)
                .filter(rev -> rev.getUser().getId().equals(user.getId()))
                .map(rev -> {
                    rev.setCompleted(true);
                    RevisionSchedule updated = revisionScheduleRepository.save(rev);
                    return ResponseEntity.ok(updated);
                })
                .orElse(ResponseEntity.status(HttpStatus.FORBIDDEN).build());
    }



    private User getAuthenticatedUser(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7);
        String username = jwtService.extractUsername(token);

        if (!jwtService.isTokenValid(token, username)) {
            throw new RuntimeException("Invalid or expired token");
        }

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
