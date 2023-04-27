package org.timetable.graph_generators;

import org.timetable.algorithm.wraps.AlgorithmConstants;
import org.timetable.model.*;
import org.timetable.pojo.*;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class IntervalRoomColorGraphGenerator implements GraphGenerator{
    private List<TimetableEdge> addEdgesBasedOnProfsAndGroups(Timetable timetable, List<TimetableNode> nodes) {
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

                Set<Group> nodeGroupsCopy = new HashSet<>(nodeGroups);
                nodeGroupsCopy.retainAll(otherNodeGroups);

                if (!nodeProfsCopy.isEmpty() || !nodeGroupsCopy.isEmpty()) {
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


        edges = new ArrayList<>(addEdgesBasedOnProfsAndGroups(timetable, nodes));
        return new TimetableGraph<>(nodes, edges);
    }


    @Override
    public List<TimetableColorIntervalRoom> createTimetableLaboratoryColors(Timetable timetable) {
        List<TimetableColorIntervalRoom> colors = new ArrayList<>();
        for (int day = 0; day < AlgorithmConstants.NUMBER_OF_DAYS; day++) {
            for (LocalTime time = AlgorithmConstants.START_TIME; time.isBefore(AlgorithmConstants.END_TIME);
                time = time.plusHours(AlgorithmConstants.GENERAL_DURATION)) {
                for (Resource resource : timetable.getResources()) {
                    if (resource.getType().equals("sem") || resource.getType().equals("lab")) {
                        colors.add(new TimetableColorIntervalRoom(day, time, resource));
                    }
                }
            }
        }
        return colors;
    }

    @Override
    public List<TimetableColorIntervalRoom> createTimetableCourseColors(Timetable timetable) {
        List<TimetableColorIntervalRoom> colors = new ArrayList<>();
        for (int day = 0; day < AlgorithmConstants.NUMBER_OF_DAYS; day++) {
            for (LocalTime time = AlgorithmConstants.START_TIME; time.isBefore(AlgorithmConstants.END_TIME);
                 time = time.plusHours(AlgorithmConstants.GENERAL_DURATION)) {
                for (Resource resource : timetable.getResources()) {
                    if (resource.getType().equals("curs")) {
                        colors.add(new TimetableColorIntervalRoom(day, time, resource));
                    }
                }
            }
        }
        return colors;
    }
}
