package org.timetable.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.timetable.pojo.Resource;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimetableColorIntervalRoom implements TimetableColor{
    private int day;
    private LocalTime time;
    private Resource resource;
}
