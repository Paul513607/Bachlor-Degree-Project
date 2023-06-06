package org.timetable.algorithm.interval_then_room.model;

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
