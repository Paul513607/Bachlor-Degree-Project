package com.timetable.timetablebe.repos;

import com.timetable.timetablebe.entities.ProfessorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProfessorRepository extends JpaRepository<ProfessorEntity, Long> {
    List<ProfessorEntity> findAllByOrderByAbbr();
}
