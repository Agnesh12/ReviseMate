package com.example.revisemate.Repository;

import com.example.revisemate.Model.RevisionSchedule;
import com.example.revisemate.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RevisionScheduleRepository extends JpaRepository<RevisionSchedule, Long> {
    List<RevisionSchedule> findByRevisionDate(LocalDate revisionDate);
    List<RevisionSchedule> findByCompleted(boolean completed);
    List<RevisionSchedule> findByRevisionDateAndCompleted(LocalDate revisionDate, boolean completed);
    List<RevisionSchedule> findByUserOrderByRevisionDateAsc(User user);
    List<RevisionSchedule> findByUserAndRevisionDateLessThanEqualAndCompletedFalseOrderByRevisionDateAsc(User user, LocalDate date);
    List<RevisionSchedule> findByUserAndRevisionDateGreaterThanAndCompletedFalseOrderByRevisionDateAsc(User user, LocalDate date);
}