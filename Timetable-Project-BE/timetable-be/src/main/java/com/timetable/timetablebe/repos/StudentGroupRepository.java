package com.timetable.timetablebe.repos;

import com.timetable.timetablebe.entities.StudentGroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentGroupRepository extends JpaRepository<StudentGroupEntity, Long> {
    @Query(value = "SELECT student_group " +
            "FROM StudentGroupEntity student_group " +
            "WHERE student_group.memberCount > 0" +
            "ORDER BY student_group.abbr")
    List<StudentGroupEntity> findAllByOrderByAbbr();
}
