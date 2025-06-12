package com.example.revisemate.Model;


import jakarta.persistence.*;

@Entity
@Table
public class RevisionSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
}
