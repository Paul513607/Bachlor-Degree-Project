package org.timetable.algorithm.partialcol.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.timetable.pojo.Event;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimeslotDataNode implements GraphNode {
    private Event event;
}
