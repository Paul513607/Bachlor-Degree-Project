package org.timetable.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimetableEdge {
    private TimetableNode node1;
    private TimetableNode node2;
}
