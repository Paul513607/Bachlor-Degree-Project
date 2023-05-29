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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
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

    public static Timetable loadTimetable(byte[] xmlFileContent) {
        Parser parser = new Parser();
        try (InputStream inputStream = new ByteArrayInputStream(xmlFileContent)) {
            parser.parse(inputStream);
        } catch (IOException e) {
            System.out.println("Error while parsing the XML file.");
            e.printStackTrace();
        }
        parser.setLinksForTimetable();
        return parser.getTimetable();
    }

    public static Map<TimetableNode, ColorDayTimeWrap> roomOnlyColoringFilePath(String xmlFilePath) {
        Timetable timetable = loadTimetable(xmlFilePath);
        return roomOnlyColoringAlgorithm(timetable);
    }

    public static Map<TimetableNode, ColorDayTimeWrap> roomOnlyColoringFileContent(byte[] xmlFileContent) {
        Timetable timetable = loadTimetable(xmlFileContent);
        return roomOnlyColoringAlgorithm(timetable);
    }

    private static Map<TimetableNode, ColorDayTimeWrap> roomOnlyColoringAlgorithm(Timetable timetable) {
        RoomColorGraphGenerator generator = new RoomColorGraphGenerator();
        TimetableGraph<TimetableNode, TimetableEdge> graph = generator.createGraph(timetable);
        List<TimetableColorRoom> laboratoryColors = generator.createTimetableLaboratoryColors(timetable);
        List<TimetableColorRoom> courseColors = generator.createTimetableCourseColors(timetable);

        RoomOnlyColoringAlgorithm algorithm =
                new RoomOnlyColoringAlgorithm(graph, laboratoryColors, courseColors);
        algorithm.colorGraph();
        algorithm.sortNodeColorMapByActorsAndDayTime();
        return algorithm.getNodeColorMap();
    }

    public static Map<TimetableNode, ColorDayTimeWrap> intervalRoomColoringGreedyFilePath(String xmlFilePath) {
        Timetable timetable = loadTimetable(xmlFilePath);
        return intervalRoomColoringGreedyAlgorithm(timetable);
    }

    public static Map<TimetableNode, ColorDayTimeWrap> intervalRoomColoringGreedyFileContent(byte[] xmlFileContent) {
        Timetable timetable = loadTimetable(xmlFileContent);
        return intervalRoomColoringGreedyAlgorithm(timetable);
    }

    private static Map<TimetableNode, ColorDayTimeWrap> intervalRoomColoringGreedyAlgorithm(Timetable timetable) {
        IntervalRoomColorGraphGenerator generator = new IntervalRoomColorGraphGenerator();
        TimetableGraph<TimetableNode, TimetableEdge> graph = generator.createGraph(timetable);
        List<TimetableColorIntervalRoom> laboratoryColors = generator.createTimetableLaboratoryColors(timetable);
        List<TimetableColorIntervalRoom> courseColors = generator.createTimetableCourseColors(timetable);

        IntervalRoomColoringAlgorithm algorithm =
                new IntervalRoomColoringAlgorithm(graph, laboratoryColors, courseColors, 1);
        algorithm.colorGraph();
        algorithm.sortNodeColorMapByActorsAndDayTime();
        return algorithm.getNodeColorMap();
    }

    public static Map<TimetableNode, ColorDayTimeWrap> intervalRoomColoringDSaturFilePath(String xmlFilePath) {
        Timetable timetable = loadTimetable(xmlFilePath);
        return intervalRoomColoringDSaturAlgorithm(timetable);
    }

    public static Map<TimetableNode, ColorDayTimeWrap> intervalRoomColoringDSaturFileContent(byte[] xmlFileContent) {
        Timetable timetable = loadTimetable(xmlFileContent);
        return intervalRoomColoringDSaturAlgorithm(timetable);
    }

    private static Map<TimetableNode, ColorDayTimeWrap> intervalRoomColoringDSaturAlgorithm(Timetable timetable) {
        IntervalRoomColorGraphGenerator generator = new IntervalRoomColorGraphGenerator();
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
            roomOnlyColoringFilePath(XML_FILEPATH);
        } else if (algorithmOption == 2) {
            intervalRoomColoringGreedyFilePath(XML_FILEPATH);
        } else if (algorithmOption == 3) {
            intervalRoomColoringDSaturFilePath(XML_FILEPATH);
        }
    }
}