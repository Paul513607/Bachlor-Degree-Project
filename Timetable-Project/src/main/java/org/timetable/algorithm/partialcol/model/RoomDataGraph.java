package org.timetable.algorithm.partialcol.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.timetable.algorithm.partialcol.TimeslotDataModel;
import org.timetable.pojo.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomDataGraph {
    // set 1 of the bipartite graph
    List<TimeslotDataNode> timeslotEvents = new ArrayList<>();
    // set 2 of the bipartite graph
    List<RoomDataNode> rooms = new ArrayList<>();
    public TimeslotDataModel timeslotDataModel;

    List<RoomDataEdge> edges = new ArrayList<>();

    public RoomDataGraph(TimeslotDataModel model, Set<TimeslotDataNode> timeslotEvents) {
        this.timeslotEvents = new ArrayList<>(timeslotEvents);
        this.timeslotDataModel = model;
        this.rooms = new ArrayList<>(model.getRooms().stream().map(RoomDataNode::new).toList());

        // add the edges
        for (TimeslotDataNode timeslotEvent : this.timeslotEvents) {
            int timeslotEventIndex = timeslotDataModel.getEvents().indexOf(timeslotEvent.getEvent());
            for (int j = 0; j < this.rooms.size(); j++) {
                if (model.getRoomSuitabilityMatrix()[timeslotEventIndex][j] == 1) {
                    edges.add(new RoomDataEdge(timeslotEvent, this.rooms.get(j)));
                }
            }
        }
    }

    public List<RoomDataNode> getNeighborsOfEvent(TimeslotDataNode node) {
        List<RoomDataNode> neighbors = new ArrayList<>();
        int indexOfNode = timeslotDataModel.getEvents().indexOf(node.getEvent());
        if (indexOfNode == -1) {
            return neighbors;
        }

        for (int i = 0; i < timeslotDataModel.getRoomSuitabilityMatrix()[indexOfNode].length; i++) {
            if (timeslotDataModel.getRoomSuitabilityMatrix()[indexOfNode][i] == 1) {
                neighbors.add(rooms.get(i));
            }
        }
        return neighbors;
    }

    public void addTimeslotEvent(TimeslotDataNode timeslotEvent) {
        timeslotEvents.add(timeslotEvent);
    }

    public void addRoom(RoomDataNode room) {
        rooms.add(room);
    }

    public void addEdge(RoomDataEdge edge) {
        edges.add(edge);
    }
}
