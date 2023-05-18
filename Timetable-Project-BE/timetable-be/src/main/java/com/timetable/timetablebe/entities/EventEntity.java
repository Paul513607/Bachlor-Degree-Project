package com.timetable.timetablebe.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "events")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String abbr;
    private String actors;
    private Integer duration;
    private Integer frequency;
    private String eventGroup;
    private String name;
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
    private String type;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "student_group_id", referencedColumnName = "id")
    private List<StudentGroupEntity> studentGroups = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "professor_id", referencedColumnName = "id")
    private List<ProfessorEntity> professors = new ArrayList<>();

    public void addStudentGroup(StudentGroupEntity studentGroup) {
        studentGroups.add(studentGroup);
    }

    public void addProf(ProfessorEntity prof) {
        professors.add(prof);
    }
}
