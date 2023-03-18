package org.timetable;

import org.timetable.model.TimetableColor;
import org.timetable.model.TimetableEdge;
import org.timetable.model.TimetableGraph;
import org.timetable.model.TimetableNode;
import org.timetable.pojo.*;
import org.timetable.util.Parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Main {
    private static final String XML_FILEPATH = "src/main/resources/export_2022-2023_semestrul_1.xml";

    public Timetable loadTimetable() {
        Parser parser = new Parser(XML_FILEPATH);
        try {
            parser.parse();
        } catch (IOException e) {
            System.out.println("Error while parsing the XML file.");
            e.printStackTrace();
        }
        parser.setLinksForTimetable();
        return parser.getTimetable();
    }

    public TimetableGraph<TimetableNode, TimetableEdge> createGraph(Timetable timetable) {
        List<TimetableNode> nodes = new ArrayList<>();
        List<TimetableEdge> edges = new ArrayList<>();

        // Add nodes
        for (Event event : timetable.getEvents()) {
            if (event.getType().equals("L") || event.getType().equals("S")) {
                TimetableNode node = new TimetableNode(event, false, null);
                nodes.add(node);
            }
        }

        // Add edges
        for (TimetableNode node : nodes) {
            Set<Prof> nodeProfs = new HashSet<>(node.getEvent().getProfList());
            Set<Group> nodeGroups = new HashSet<>(node.getEvent().getGroupList());
            for (TimetableNode otherNode : nodes) {
                Set<Prof> otherNodeProfs = new HashSet<>(otherNode.getEvent().getProfList());
                Set<Group> otherNodeGroups = new HashSet<>(otherNode.getEvent().getGroupList());
                if (!node.equals(otherNode)) {
                    if (nodeProfs.retainAll(otherNodeProfs) || nodeGroups.retainAll(otherNodeGroups)) {
                        TimetableEdge edge = new TimetableEdge(node, otherNode);
                        edges.add(edge);
                    }
                }
            }
        }

        // Add available colors
        List<TimetableColor> colors = new ArrayList<>();
        for (Resource resource : timetable.getResources()) {
            if (resource.getType().equals("sem") || resource.getType().equals("lab")) {
                colors.add(new TimetableColor(resource));
            }
        }

        return new TimetableGraph<>(nodes, edges, colors);
    }

    public static void main(String[] args) {
        Main app = new Main();
        Timetable timetable = app.loadTimetable();
        TimetableGraph<TimetableNode, TimetableEdge> graph = app.createGraph(timetable);
    }
}