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

    @ManyToMany(mappedBy = "eventList", cascade = CascadeType.MERGE)
    private List<StudentGroupEntity> studentGroups = new ArrayList<>();
    @ManyToMany(mappedBy = "eventList", cascade = CascadeType.MERGE)
    private List<ProfessorEntity> professors = new ArrayList<>();

    @OneToOne(mappedBy = "event", cascade = CascadeType.MERGE)
    private AssignedEventEntity assignedEvent;

    public void addStudentGroup(StudentGroupEntity studentGroup) {
        studentGroups.add(studentGroup);
    }

    public void addProf(ProfessorEntity prof) {
        professors.add(prof);
    }
}
