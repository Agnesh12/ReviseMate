package com.example.revisemate.Repository;


import com.example.revisemate.Model.RevisionSchedule;
import jdk.jfr.Registered;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
@Repository
public interface RevisionScheduleRepository extends JpaRepository<RevisionSchedule, Long> {
    List<RevisionSchedule> findByRevisionDate(LocalDate revisionDate);
    List<RevisionSchedule> findByCompleted(boolean completed);
    List<RevisionSchedule> findByRevisionDateAndCompleted(LocalDate revisionDate, boolean completed);
}
