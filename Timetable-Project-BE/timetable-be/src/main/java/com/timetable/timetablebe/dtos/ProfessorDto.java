package com.timetable.timetablebe.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfessorDto {
    private String abbr;
    private String email;
    private String name;
    private String notes;
    private String parent;
    private String prefix;
}
