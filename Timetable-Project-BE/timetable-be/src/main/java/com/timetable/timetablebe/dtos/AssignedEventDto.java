package com.timetable.timetablebe.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssignedEventDto {
    private EventDto event;
    private ResourceDto resource;
    private Integer day;
    private LocalTime time;
}
