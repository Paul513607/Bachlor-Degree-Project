package com.timetable.timetablebe.repos;

import com.timetable.timetablebe.entities.EventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<EventEntity, Long> {
    Optional<EventEntity> findByAbbr(String abbr);
    @Query(value = "SELECT event FROM EventEntity event " +
            "LEFT JOIN AssignedEventEntity assignedEvent " +
            "ON event.id = assignedEvent.event.id " +
            "WHERE assignedEvent.event.id IS NULL " +
            "ORDER BY event.name")
    List<EventEntity> findAllUnassignedEvents();
}
