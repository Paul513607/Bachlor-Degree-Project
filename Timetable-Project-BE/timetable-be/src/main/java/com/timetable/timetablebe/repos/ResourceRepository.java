package com.timetable.timetablebe.repos;

import com.timetable.timetablebe.entities.ResourceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResourceRepository extends JpaRepository<ResourceEntity, Long> {
    Optional<ResourceEntity> findByAbbr(String abbr);

    @Query(value = "SELECT resource FROM ResourceEntity resource " +
            "WHERE resource.type IN ('curs', 'lab', 'sem') " +
            "AND resource.capacity > 0 " +
            "ORDER BY resource.abbr")
    List<ResourceEntity> findAllSuitableRooms();
}
