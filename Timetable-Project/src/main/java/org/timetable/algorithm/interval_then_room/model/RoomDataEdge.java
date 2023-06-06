package org.timetable.algorithm.interval_then_room.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomDataEdge {
    private TimeslotDataNode timeslotEvent;
    private RoomDataNode resource;

    public boolean containsNode(GraphNode node) {
        return timeslotEvent.equals(node) || resource.equals(node);
    }

    public GraphNode getNeighbor(GraphNode node) {
        if (!containsNode(node)) {
            return null;
        }

        if (timeslotEvent.equals(node)) {
            return resource;
        }
        return timeslotEvent;
    }
}
