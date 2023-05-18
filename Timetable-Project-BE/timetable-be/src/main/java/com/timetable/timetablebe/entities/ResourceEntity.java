package com.timetable.timetablebe.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

@Entity
@Table(name = "resources")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResourceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String abbr;
    private Integer capacity;
    private String name;
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
    private Integer quantity;
    private String type;
}
