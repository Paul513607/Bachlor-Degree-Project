package org.timetable.algorithm.interval_then_room;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.timetable.algorithm.interval_then_room.model.*;

import java.util.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomBipartiteSolver {
    private RoomDataGraph graph;

    private Map<TimeslotDataNode, RoomDataNode> eventPairs = new HashMap<>();
    private Map<RoomDataNode, TimeslotDataNode> roomPairs = new HashMap<>();
    private int[] levels;

    public RoomBipartiteSolver(RoomDataGraph graph) {
        this.graph = graph;
        levels = new int[graph.getTimeslotEvents().size() + 1];
    }

    public int hopcroftKarpSolver() {
        for (TimeslotDataNode node : graph.getTimeslotEvents()) {
            eventPairs.put(node, null);
        }
        eventPairs.put(null, null);
        for (RoomDataNode node : graph.getRooms()) {
            roomPairs.put(node, null);
        }
        roomPairs.put(null, null);

        int matchingLength = 0;
        while (BFS()) {
            for (int i = 0; i < graph.getTimeslotEvents().size(); i++) {
                TimeslotDataNode node = graph.getTimeslotEvents().get(i);
                if (eventPairs.get(node) == null && DFS(node)) {
                    matchingLength++;
                }
            }
        }

        return matchingLength;
    }

    public boolean BFS() {
        Queue<TimeslotDataNode> queue = new LinkedList<>();
        for (int i = 0; i < graph.getTimeslotEvents().size(); i++) {
            TimeslotDataNode node = graph.getTimeslotEvents().get(i);
            if (eventPairs.get(node) == null) {
                levels[i] = 0;
                queue.add(node);
            } else {
                levels[i] = Integer.MAX_VALUE;
            }
        }
        levels[graph.getTimeslotEvents().size()] = Integer.MAX_VALUE;

        while (!queue.isEmpty()) {
            TimeslotDataNode currentNode = queue.poll();
            int currentNodeIndex = graph.getTimeslotEvents().indexOf(currentNode);
            if (currentNodeIndex == -1) {
                currentNodeIndex = graph.getTimeslotEvents().size();
            }

            if (levels[currentNodeIndex] < levels[graph.getTimeslotEvents().size()]) {
                for (RoomDataNode neighbor : graph.getNeighborsOfEvent(currentNode)) {
                    int neighborIndex = graph.getRooms().indexOf(neighbor);
                    TimeslotDataNode nextNode = roomPairs.get(neighbor);
                    int nextNodeIndex = graph.getTimeslotEvents().indexOf(nextNode);
                    if (nextNodeIndex == -1) {
                        nextNodeIndex = graph.getTimeslotEvents().size();
                    }

                    if (levels[nextNodeIndex] == Integer.MAX_VALUE) {
                        levels[nextNodeIndex] = levels[currentNodeIndex] + 1;
                        queue.add(nextNode);
                    }
                }
            }
        }
        return levels[graph.getTimeslotEvents().size()] != Integer.MAX_VALUE;
    }

    public boolean DFS(TimeslotDataNode node) {
        if (node == null) {
            return true;
        }
        int nodeIndex = graph.getTimeslotEvents().indexOf(node);
        if (nodeIndex == -1) {
            nodeIndex = graph.getTimeslotEvents().size();
        }
        for (RoomDataNode neighbor : graph.getNeighborsOfEvent(node)) {
            TimeslotDataNode nextNode = roomPairs.get(neighbor);
            int nextNodeIndex = graph.getTimeslotEvents().indexOf(nextNode);
            if (nextNodeIndex == -1) {
                nextNodeIndex = graph.getTimeslotEvents().size();
            }

            if (levels[nextNodeIndex] == levels[nodeIndex] + 1) {
                if (DFS(nextNode)) {
                    roomPairs.put(neighbor, node);
                    eventPairs.put(node, neighbor);
                    return true;
                }
            }
        }

        levels[nodeIndex] = Integer.MAX_VALUE;
        return false;
    }

    public void printSolution() {
        for (TimeslotDataNode node : eventPairs.keySet()) {
            System.out.println(node + "\n\t" + eventPairs.get(node));
        }
    }
}
