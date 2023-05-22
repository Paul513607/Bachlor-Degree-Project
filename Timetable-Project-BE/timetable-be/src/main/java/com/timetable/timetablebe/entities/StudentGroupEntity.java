package com.timetable.timetablebe.entities;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "student_groups")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentGroupEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String abbr;
    private Integer memberCount;
    private String name;
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
    private String parent;

    @ManyToMany(cascade = CascadeType.MERGE)
    @JoinTable(name = "event_student_groups",
            joinColumns = @JoinColumn(name = "student_group_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id"))
    private List<EventEntity> eventList = new ArrayList<>();

    public void addEvent(EventEntity event) {
        eventList.add(event);
    }
}
