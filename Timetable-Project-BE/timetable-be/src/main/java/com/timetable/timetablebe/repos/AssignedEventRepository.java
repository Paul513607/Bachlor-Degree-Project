package com.timetable.timetablebe.repos;

import com.timetable.timetablebe.entities.AssignedEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssignedEventRepository extends JpaRepository<AssignedEventEntity, Long> {
    @Query(value = "SELECT assigned_event FROM AssignedEventEntity assigned_event " +
            "JOIN assigned_event.event event " +
            "JOIN event.studentGroups student_group " +
            "WHERE LOWER(student_group.abbr) = LOWER(:studentGroupAbbr) " +
            "ORDER BY assigned_event.day, assigned_event.time")
    List<AssignedEventEntity> findAllByStudentGroupAbbr(String studentGroupAbbr);
}
