package com.example.revisemate.Model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "topic")
public class Topic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String title;
    private String description;
    private LocalDate createdDate;

    private LocalDate revisionDateDay3;
    private LocalDate revisionDateDay7; // Ensure this field exists and is correctly mapped

    @Column(name = "notification_stage", nullable = false)
    private int notificationStage = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference("user-topics")
    private User user;

    // IMPORTANT: cascade = CascadeType.ALL and orphanRemoval = true
    // This ensures that when a Topic is deleted, its associated RevisionSchedules are also deleted.
    @OneToMany(mappedBy = "topic", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("topic-revisions")
    private List<RevisionSchedule> revisionSchedules = new ArrayList<>();


    public void addRevisionSchedule(RevisionSchedule revisionSchedule) {
        revisionSchedules.add(revisionSchedule);
        revisionSchedule.setTopic(this);
    }

    public void removeRevisionSchedule(RevisionSchedule revisionSchedule) {
        revisionSchedules.remove(revisionSchedule);
        revisionSchedule.setTopic(null);
    }
}