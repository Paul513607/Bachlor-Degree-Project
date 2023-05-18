package com.timetable.timetablebe.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResourceDto {
    private String abbr;
    private Integer capacity;
    private String name;
    private String notes;
    private Integer quantity;
    private String type;
}
