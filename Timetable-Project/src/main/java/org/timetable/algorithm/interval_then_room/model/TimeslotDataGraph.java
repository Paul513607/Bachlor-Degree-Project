package org.timetable.algorithm.interval_then_room.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.timetable.algorithm.interval_then_room.datamodel.TimeslotDataModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimeslotDataGraph {
    private List<TimeslotDataNode> nodes = new ArrayList<>();
    private List<TimeslotDataEdge> edges = new ArrayList<>();
    private int[][] adjacencyMatrix;

    public TimeslotDataGraph(TimeslotDataModel model) {
        nodes = model.getEvents().stream()
                .map(event -> new TimeslotDataNode(event))
                .collect(Collectors.toList());

        for (int i = 0; i < model.getConstraintMatrix().length; i++) {
            for (int j = 0; j < model.getConstraintMatrix()[i].length; j++) {
                if (model.getConstraintMatrix()[i][j] == 1) {
                    edges.add(new TimeslotDataEdge(nodes.get(i), nodes.get(j)));
                }
            }
        }

        adjacencyMatrix = new int[nodes.size()][nodes.size()];
        adjacencyMatrix = Arrays.copyOf(model.getConstraintMatrix(), nodes.size());
    }

    public void addNode(TimeslotDataNode node) {
        nodes.add(node);
    }

    public void addEdge(TimeslotDataEdge edge) {
        edges.add(edge);
    }

    public void addEdge(TimeslotDataNode node1, TimeslotDataNode node2) {
        edges.add(new TimeslotDataEdge(node1, node2));
    }

    public List<TimeslotDataNode> getNeighbors(TimeslotDataNode node) {
        List<TimeslotDataNode> neighbors = new ArrayList<>();
        int indexOfNode = nodes.indexOf(node);
        for (int i = 0; i < adjacencyMatrix.length; i++) {
            if (adjacencyMatrix[indexOfNode][i] == 1) {
                neighbors.add(nodes.get(i));
            }
        }
        return neighbors;
    }

    public List<TimeslotDataNode> getNeighbors_v2(TimeslotDataNode node) {
        List<TimeslotDataNode> neighbors = new ArrayList<>();
        for (TimeslotDataEdge edge : edges) {
            if (edge.containsNode(node)) {
                neighbors.add(edge.getNeighbor(node));
            }
        }
        return neighbors;
    }
}
