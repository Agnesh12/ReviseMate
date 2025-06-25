package com.example.revisemate.Model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String username;
    private String password;
    private String email;
    private String role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference("user-topics") // IMPORTANT: Unique name for User <-> Topic relationship
    private List<Topic> topics = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference("user-revisions") // IMPORTANT: Unique name for User <-> RevisionSchedule relationship
    private List<RevisionSchedule> revisions = new ArrayList<>();


    public void addTopic(Topic topic) {
        topics.add(topic);
        topic.setUser(this);
    }

    public void removeTopic(Topic topic) {
        topics.remove(topic);
        topic.setUser(null);
    }

    public void addRevision(RevisionSchedule revision) {
        revisions.add(revision);
        revision.setUser(this);
    }

    public void removeRevision(RevisionSchedule revision) {
        revisions.remove(revision);
        revision.setUser(null);
    }
}