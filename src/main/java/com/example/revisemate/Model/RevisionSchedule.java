package com.example.revisemate.Model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "revision")
public class RevisionSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate revisionDate;

    private boolean completed = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public RevisionSchedule() {}

    public RevisionSchedule(LocalDate revisionDate, boolean completed) {
        this.revisionDate = revisionDate;
        this.completed = completed;
    }

    public Long getId() {
        return id;
    }

    public LocalDate getRevisionDate() {
        return revisionDate;
    }

    public void setRevisionDate(LocalDate revisionDate) {
        this.revisionDate = revisionDate;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
