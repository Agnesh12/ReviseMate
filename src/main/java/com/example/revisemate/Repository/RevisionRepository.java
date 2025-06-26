package com.example.revisemate.Repository;

import com.example.revisemate.Model.Revision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

// RevisionRepository.java
public interface RevisionRepository extends JpaRepository<Revision, Long> {

    @Query("""
           select r from Revision r
           where r.topic.user.id = :userId
             and r.dueDate <= :today
             and r.completed = false
           order by r.dueDate asc
           """)
    List<Revision> dueToday(Long userId, LocalDate today);

    @Query("""
           select r from Revision r
           where r.topic.user.id = :userId
           order by r.dueDate desc
           """)
    List<Revision> allForUser(Long userId);

    @Query("""
           select count(t) from Topic t 
           where t.user.id = :userId
           """)
    long countTopics(Long userId);

    @Query("""
           select count(r) from Revision r
           where r.topic.user.id = :userId
             and r.dueDate <= :today
             and r.completed = false
           """)
    long countTodayRevisions(Long userId, LocalDate today);

    @Query("""
           select count(r) from Revision r
           where r.topic.user.id = :userId
             and r.completed = true
           """)
    long countCompletedRevisions(Long userId);
}

