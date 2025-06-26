package com.example.revisemate.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

// Revision.java
@Entity
@Table(name = "revisions") @Getter
@Setter
@NoArgsConstructor
public class Revision {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(nullable = false)
    private Topic topic;

    private Integer revisionNumber;

    @Column(nullable = false) private LocalDate dueDate;

    private boolean completed = false;
    private LocalDateTime completedAt;
}

