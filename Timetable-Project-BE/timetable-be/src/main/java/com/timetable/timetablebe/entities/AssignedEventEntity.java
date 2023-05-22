package com.timetable.timetablebe.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalTime;

@Entity
@Table(name = "assigned_events")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssignedEventEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "event_id", referencedColumnName = "id")
    private EventEntity event;
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "resource_id", referencedColumnName = "id")
    private ResourceEntity resource;
    private Integer day;
    private LocalTime time;

    public AssignedEventEntity(EventEntity event, ResourceEntity resource, int day, LocalTime time) {
        this.event = event;
        this.resource = resource;
        this.day = day;
        this.time = time;
    }
}
