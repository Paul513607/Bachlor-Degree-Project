package org.timetable.algorithm.partialcol.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Timeslot {
    private int day;
    private LocalTime startTime;
    private LocalTime endTime;
}
