package com.timetable.timetablebe.repos;

import com.timetable.timetablebe.entities.TimetableFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TimetableFileRepository extends JpaRepository<TimetableFileEntity, Long> {
    Optional<TimetableFileEntity> findFirstByOrderByTimestampAddedDesc();

    Optional<TimetableFileEntity> findByName(String name);
}
