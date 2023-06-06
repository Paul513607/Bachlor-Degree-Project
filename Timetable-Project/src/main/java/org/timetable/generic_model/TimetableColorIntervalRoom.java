package org.timetable.generic_model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.timetable.pojo.Resource;

import java.time.LocalTime;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimetableColorIntervalRoom implements TimetableColor{
    private int day;
    private LocalTime time;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimetableColorIntervalRoom that = (TimetableColorIntervalRoom) o;
        return day == that.day && Objects.equals(time, that.time) || Objects.equals(resource, that.resource);
    }

    @Override
    public int hashCode() {
        return Objects.hash(day, time, resource);
    }

    private Resource resource;
}
