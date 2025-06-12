package com.example.revisemate.Model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "topic")
public class Topic {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private long id;
    private String title;
    private String description;
    private LocalDate createdDate;
    private LocalDate revisedDate;

    public Topic(){}
    public Topic(String title, String description, LocalDate createdDate, LocalDate revisedDate ) {
        this.title = title;
        this.description = description;
        this.createdDate = createdDate;
        this.revisedDate = revisedDate;
    }
    public long getId() {
        return id;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getTitle() {
        return title;
    }
    public void setDescription(String discription) {
        this.description = description;
    }
    public String getDescription() {
        return description;
    }
    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }
    public LocalDate getCreatedDate() {
        return createdDate;
    }
    public void setRevisedDate(LocalDate revisedDate) {
        this.revisedDate = revisedDate;
    }
    public LocalDate getRevisedDate(){
        return revisedDate;
    }
}
