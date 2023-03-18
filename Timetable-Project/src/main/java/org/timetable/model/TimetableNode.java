package org.timetable.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.timetable.pojo.Event;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimetableNode {
    private Event event;
    private boolean isAssigned;
    private TimetableColor color;
}
