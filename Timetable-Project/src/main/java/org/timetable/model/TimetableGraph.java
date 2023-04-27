package org.timetable.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimetableGraph<V extends TimetableNode, E extends TimetableEdge> {
    private List<TimetableNode> nodes = new ArrayList<>();
    private List<TimetableEdge> edges = new ArrayList<>();

    public boolean containsEdge(V node, V otherNode) {
        for (TimetableEdge edge : edges) {
            if (edge.getNode1().equals(node) && edge.getNode2().equals(otherNode) ||
                    edge.getNode1().equals(otherNode) && edge.getNode2().equals(node)) {
                return true;
            }
        }
        return false;
    }

    public Set<TimetableNode> getNeighbours(V node) {
        Set<TimetableNode> neighbours = new HashSet<>();
        for (TimetableEdge edge : edges) {
            if (edge.containsNode(node)) {
                if (node.equals(edge.getNode1())) {
                    neighbours.add(edge.getNode2());
                } else {
                    neighbours.add(edge.getNode1());
                }
            }
        }
        return neighbours;
    }
}
