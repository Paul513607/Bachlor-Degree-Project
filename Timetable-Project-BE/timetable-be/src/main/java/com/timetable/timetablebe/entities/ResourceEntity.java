package com.timetable.timetablebe.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(mappedBy = "resource", cascade = CascadeType.MERGE)
    private List<AssignedEventEntity> assignedEvent = new ArrayList<>();

    public void addAssignedEvent(AssignedEventEntity assignedEvent) {
        this.assignedEvent.add(assignedEvent);
    }
}
