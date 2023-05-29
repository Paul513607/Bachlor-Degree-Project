package com.timetable.timetablebe.repos;

import com.timetable.timetablebe.entities.AssignedEventEntity;
import com.timetable.timetablebe.entities.EventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssignedEventRepository extends JpaRepository<AssignedEventEntity, Long> {
    @Query(value = "SELECT assigned_event FROM AssignedEventEntity assigned_event " +
            "JOIN assigned_event.event event " +
            "JOIN event.studentGroups student_group " +
            "WHERE LOWER(student_group.abbr) = LOWER(:studentGroupAbbr) " +
            "ORDER BY assigned_event.day, assigned_event.time, student_group.abbr")
    List<AssignedEventEntity> findAllByStudentGroupAbbr(String studentGroupAbbr);

    @Query(value = "SELECT assigned_event FROM AssignedEventEntity assigned_event " +
            "JOIN assigned_event.event event " +
            "JOIN event.professors professor " +
            "WHERE LOWER(professor.abbr) = LOWER(:profAbbr) " +
            "ORDER BY assigned_event.day, assigned_event.time, professor.abbr")
    List<AssignedEventEntity> findAllByProfAbbr(String profAbbr);

    @Query(value = "SELECT assigned_event FROM AssignedEventEntity assigned_event " +
            "JOIN assigned_event.resource resource " +
            "WHERE LOWER(resource.abbr) = LOWER(:roomAbbr) " +
            "ORDER BY assigned_event.day, assigned_event.time, resource.abbr")
    List<AssignedEventEntity> findAllByRoomAbbr(String roomAbbr);

    Optional<AssignedEventEntity> findFirstByEvent(EventEntity event);

    @Query(value = "SELECT assigned_event FROM AssignedEventEntity assigned_event " +
            "ORDER BY assigned_event.day, assigned_event.time")
    List<AssignedEventEntity> findAllOrderByDayAndTime();
}
