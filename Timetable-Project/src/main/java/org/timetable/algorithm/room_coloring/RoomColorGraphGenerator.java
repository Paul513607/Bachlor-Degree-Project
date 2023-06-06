package org.timetable.algorithm.room_coloring;

import org.timetable.algorithm.GraphGenerator;
import org.timetable.generic_model.TimetableColorRoom;
import org.timetable.generic_model.TimetableEdge;
import org.timetable.generic_model.TimetableGraph;
import org.timetable.generic_model.TimetableNode;
import org.timetable.pojo.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RoomColorGraphGenerator implements GraphGenerator {
    public List<TimetableEdge> addEdgesBasedOnProfs(Timetable timetable, List<TimetableNode> nodes) {
        List<TimetableEdge> edges = new ArrayList<>();

        // Add edges
        for (TimetableNode node : nodes) {
            Set<Prof> nodeProfs = new HashSet<>(node.getEvent().getProfList());
            Set<Group> nodeGroups = new HashSet<>(node.getEvent().getGroupList());
            for (TimetableNode otherNode : nodes) {
                Set<Prof> otherNodeProfs = new HashSet<>(otherNode.getEvent().getProfList());
                Set<Group> otherNodeGroups = new HashSet<>(otherNode.getEvent().getGroupList());
                if (node.equals(otherNode)) {
                    continue;
                }

                Set<Prof> nodeProfsCopy = new HashSet<>(nodeProfs);
                nodeProfsCopy.retainAll(otherNodeProfs);
                if (!nodeProfsCopy.isEmpty() /*|| nodeGroups.retainAll(otherNodeGroups) */) {
                    TimetableEdge edge = new TimetableEdge(node, otherNode);
                    edges.add(edge);
                }
            }
        }

        return edges;
    }

    @Override
    public TimetableGraph<TimetableNode, TimetableEdge> createGraph(Timetable timetable) {
        List<TimetableNode> nodes = new ArrayList<>();
        List<TimetableEdge> edges;

        // Add nodes
        for (Event event : timetable.getEvents()) {
            if (event.getType().equals("C") || event.getType().equals("L") ||
                    event.getType().equals("S")) {
                TimetableNode node = new TimetableNode(event, false, null);
                nodes.add(node);
            }
        }

        edges = new ArrayList<>(addEdgesBasedOnProfs(timetable, nodes));
        return new TimetableGraph<>(nodes, edges);
    }

    @Override
    public List<TimetableColorRoom> createTimetableLaboratoryColors(Timetable timetable) {
        // Add available colors
        List<TimetableColorRoom> colors = new ArrayList<>();
        for (Resource resource : timetable.getResources()) {
            if (resource.getType().equals("sem") || resource.getType().equals("lab")) {
                colors.add(new TimetableColorRoom(resource));
            }
        }

        return colors;
    }

    @Override
    public List<TimetableColorRoom> createTimetableCourseColors(Timetable timetable) {
        // Add available colors
        List<TimetableColorRoom> colors = new ArrayList<>();
        for (Resource resource : timetable.getResources()) {
            if (resource.getType().equals("curs")) {
                colors.add(new TimetableColorRoom(resource));
            }
        }

        return colors;
    }
}
