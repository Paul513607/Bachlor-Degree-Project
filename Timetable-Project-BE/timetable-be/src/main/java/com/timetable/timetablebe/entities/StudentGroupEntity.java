package com.timetable.timetablebe.entities;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
}
