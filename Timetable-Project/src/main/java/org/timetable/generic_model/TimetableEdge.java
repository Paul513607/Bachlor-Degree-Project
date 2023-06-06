package org.timetable.generic_model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimetableEdge {
    private TimetableNode node1;
    private TimetableNode node2;

    public boolean containsNode(TimetableNode node) {
        return node1.equals(node) || node2.equals(node);
    }
}
