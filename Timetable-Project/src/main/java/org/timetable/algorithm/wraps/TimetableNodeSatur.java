package org.timetable.algorithm.wraps;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.timetable.model.TimetableNode;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimetableNodeSatur {
    private Integer saturation;
    private Integer degree;
    private TimetableNode node;
}
