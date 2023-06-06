package org.timetable.algorithm.interval_then_room.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimeslotDataEdge {
    private TimeslotDataNode node1;
    private TimeslotDataNode node2;

    public boolean containsNode(TimeslotDataNode node) {
        return node1.equals(node) || node2.equals(node);
    }

    public TimeslotDataNode getNeighbor(TimeslotDataNode node) {
        if (!containsNode(node)) {
            return null;
        }

        if (node1.equals(node)) {
            return node2;
        }
        return node1;
    }
}
