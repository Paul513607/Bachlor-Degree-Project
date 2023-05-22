package com.timetable.timetablebe.entities;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "professors")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfessorEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String abbr;
    private String email;
    private String name;
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
    private String parent;
    private String prefix;

    @ManyToMany(cascade = CascadeType.MERGE)
    @JoinTable(name = "event_professors",
            joinColumns = @JoinColumn(name = "professor_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id"))
    private List<EventEntity> eventList = new ArrayList<>();

    public void addEvent(EventEntity event) {
        eventList.add(event);
    }
}
