package com.timetable.timetablebe.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import java.io.File;
import java.time.LocalDateTime;

@Entity
@Table(name = "timetable_files")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimetableFileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(columnDefinition = "bytea")
    private byte[] file;
    @Column(unique = true)
    private String name;
    private Long timestampAdded;
}
