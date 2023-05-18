package org.timetable;

import org.timetable.algorithm.IntervalRoomColoringAlgorithm;
import org.timetable.algorithm.RoomOnlyColoringAlgorithm;
import org.timetable.algorithm.wraps.ColorDayTimeWrap;
import org.timetable.graph_generators.GraphGenerator;
import org.timetable.graph_generators.IntervalRoomColorGraphGenerator;
import org.timetable.graph_generators.RoomColorGraphGenerator;
import org.timetable.model.*;
import org.timetable.pojo.*;
import org.timetable.util.Parser;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {
    private static final String XML_FILEPATH = "src/main/resources/export_2022-2023_semestrul_1.xml";

    public static Timetable loadTimetable(String xmlFilePath) {
        Parser parser = new Parser(xmlFilePath);
        try {
            parser.parse();
        } catch (IOException e) {
            System.out.println("Error while parsing the XML file.");
            e.printStackTrace();
        }
        parser.setLinksForTimetable();
        return parser.getTimetable();
    }

    public static Map<TimetableNode, ColorDayTimeWrap> roomOnlyColoring(String xmlFilePath) {
        RoomColorGraphGenerator generator = new RoomColorGraphGenerator();
        Timetable timetable = loadTimetable(xmlFilePath);
        TimetableGraph<TimetableNode, TimetableEdge> graph = generator.createGraph(timetable);
        List<TimetableColorRoom> laboratoryColors = generator.createTimetableLaboratoryColors(timetable);
        List<TimetableColorRoom> courseColors = generator.createTimetableCourseColors(timetable);

        RoomOnlyColoringAlgorithm algorithm =
                new RoomOnlyColoringAlgorithm(graph, laboratoryColors, courseColors);
        algorithm.colorGraph();
        algorithm.sortNodeColorMapByActorsAndDayTime();
        return algorithm.getNodeColorMap();
    }

    public static Map<TimetableNode, ColorDayTimeWrap> intervalRoomColoringGreedy(String xmlFilePath) {
        IntervalRoomColorGraphGenerator generator = new IntervalRoomColorGraphGenerator();
        Timetable timetable = loadTimetable(xmlFilePath);
        TimetableGraph<TimetableNode, TimetableEdge> graph = generator.createGraph(timetable);
        List<TimetableColorIntervalRoom> laboratoryColors = generator.createTimetableLaboratoryColors(timetable);
        List<TimetableColorIntervalRoom> courseColors = generator.createTimetableCourseColors(timetable);

        IntervalRoomColoringAlgorithm algorithm =
                new IntervalRoomColoringAlgorithm(graph, laboratoryColors, courseColors, 1);
        algorithm.colorGraph();
        algorithm.sortNodeColorMapByActorsAndDayTime();
        return algorithm.getNodeColorMap();
    }

    public static Map<TimetableNode, ColorDayTimeWrap> intervalRoomColoringDSatur(String xmlFilePath) {
        IntervalRoomColorGraphGenerator generator = new IntervalRoomColorGraphGenerator();
        Timetable timetable = loadTimetable(xmlFilePath);
        TimetableGraph<TimetableNode, TimetableEdge> graph = generator.createGraph(timetable);
        List<TimetableColorIntervalRoom> laboratoryColors = generator.createTimetableLaboratoryColors(timetable);
        List<TimetableColorIntervalRoom> courseColors = generator.createTimetableCourseColors(timetable);

        IntervalRoomColoringAlgorithm algorithm =
                new IntervalRoomColoringAlgorithm(graph, laboratoryColors, courseColors, 2);
        algorithm.colorGraph();
        algorithm.sortNodeColorMapByActorsAndDayTime();
        return algorithm.getNodeColorMap();
    }

    public static void main(String[] args) {
        Main app = new Main();

        Scanner scanner = new Scanner(System.in);
        System.out.println(
                     """
                     Choose the generation algorithm:
                     1. Room only coloring
                     2. Interval room coloring - Greedy
                     3. Interval room coloring - DSatur
                     """);
        int algorithmOption = 0;
        try {
            algorithmOption = scanner.nextInt();
        } catch (Exception e) {
            System.out.println("Invalid input.");
            e.printStackTrace();
        }

        if (algorithmOption == 1) {
            app.roomOnlyColoring(XML_FILEPATH);
        } else if (algorithmOption == 2) {
            app.intervalRoomColoringGreedy(XML_FILEPATH);
        } else if (algorithmOption == 3) {
            app.intervalRoomColoringDSatur(XML_FILEPATH);
        }
    }
}