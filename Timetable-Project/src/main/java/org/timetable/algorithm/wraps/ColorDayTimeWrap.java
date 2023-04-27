package org.timetable.algorithm.wraps;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.timetable.model.TimetableColorRoom;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ColorDayTimeWrap {
    private TimetableColorRoom color;
    private int day;
    private int time;

    @Override
    public String toString() {
        return "ColorDayTimeWrapper{" +
                "color=" + color +
                ", day=" + day +
                ", time=" + time +
                '}';
    }
}
