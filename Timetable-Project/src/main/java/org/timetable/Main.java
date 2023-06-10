package org.timetable;

import org.timetable.algorithm.interval_room_sim.IntervalRoomColoringAlgorithm;
import org.timetable.algorithm.interval_then_room.datamodel.TimeslotDataModel;
import org.timetable.algorithm.room_coloring.RoomOnlyColoringAlgorithm;
import org.timetable.algorithm.interval_then_room.*;
import org.timetable.algorithm.interval_then_room.model.RoomDataGraph;
import org.timetable.algorithm.interval_then_room.model.RoomDataNode;
import org.timetable.algorithm.interval_then_room.model.Timeslot;
import org.timetable.algorithm.interval_then_room.model.TimeslotDataNode;
import org.timetable.algorithm.wraps.ColorDayTimeWrap;
import org.timetable.algorithm.interval_room_sim.IntervalRoomColorGraphGenerator;
import org.timetable.algorithm.room_coloring.RoomColorGraphGenerator;
import org.timetable.generic_model.*;
import org.timetable.pojo.*;
import org.timetable.util.AlgorithmConstants;
import org.timetable.util.Parser;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.time.LocalTime;
import java.util.*;

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

    public static Map<TimetableNode, ColorDayTimeWrap> roomOnlyColoringFilePath(
            String xmlFilePath, int algorithmOption, boolean useSorting, boolean shuffle) {
        Timetable timetable = loadTimetable(xmlFilePath);
        return roomOnlyColoringAlgorithm(timetable, algorithmOption, useSorting, shuffle);
    }

    public static Map<TimetableNode, ColorDayTimeWrap> roomOnlyColoringFileContent(
            byte[] xmlFileContent, int algorithmOption, boolean useSorting, boolean shuffle) {
        Timetable timetable = loadTimetable(xmlFileContent);
        return roomOnlyColoringAlgorithm(timetable, algorithmOption, useSorting, shuffle);
    }

    private static Map<TimetableNode, ColorDayTimeWrap> roomOnlyColoringAlgorithm(
            Timetable timetable, int algorithmOption, boolean useSorting, boolean shuffle) {
        RoomColorGraphGenerator generator = new RoomColorGraphGenerator();
        TimetableGraph<TimetableNode, TimetableEdge> graph = generator.createGraph(timetable);
        List<TimetableColorRoom> laboratoryColors = generator.createTimetableLaboratoryColors(timetable);
        List<TimetableColorRoom> courseColors = generator.createTimetableCourseColors(timetable);

        RoomOnlyColoringAlgorithm algorithm =
                new RoomOnlyColoringAlgorithm(graph, laboratoryColors, courseColors);
        algorithm.colorGraph(algorithmOption, useSorting, shuffle);
        algorithm.sortNodeColorMapByActorsAndDayTime();
        return algorithm.getNodeColorMap();
    }

    public static Map<TimetableNode, ColorDayTimeWrap> intervalRoomColoringFilePath(
                    String xmlFilePath, int algorithmOption, boolean useSorting, boolean shuffle) {
        Timetable timetable = loadTimetable(xmlFilePath);
        return intervalRoomColoringAlgorithm(timetable, algorithmOption, useSorting, shuffle);
    }

    public static Map<TimetableNode, ColorDayTimeWrap> intervalRoomColoringFileContent(
                    byte[] xmlFileContent, int algorithmOption, boolean useSorting, boolean shuffle) {
        Timetable timetable = loadTimetable(xmlFileContent);
        return intervalRoomColoringAlgorithm(timetable, algorithmOption, useSorting, shuffle);
    }

    private static Map<TimetableNode, ColorDayTimeWrap> intervalRoomColoringAlgorithm(
                    Timetable timetable, int algorithmOption, boolean useSorting, boolean shuffle) {
        IntervalRoomColorGraphGenerator generator = new IntervalRoomColorGraphGenerator();
        TimetableGraph<TimetableNode, TimetableEdge> graph = generator.createGraph(timetable);
        List<TimetableColorIntervalRoom> laboratoryColors = generator.createTimetableLaboratoryColors(timetable);
        List<TimetableColorIntervalRoom> courseColors = generator.createTimetableCourseColors(timetable);

        IntervalRoomColoringAlgorithm algorithm =
                new IntervalRoomColoringAlgorithm(graph, laboratoryColors, courseColors, algorithmOption);
        algorithm.colorGraph(algorithmOption, useSorting, shuffle);
        algorithm.sortNodeColorMapByActorsAndDayTime();
        return algorithm.getNodeColorMap();
    }

    public static Map<TimetableNode, ColorDayTimeWrap> intervalColoringTwoStepFilePath(
            String xmlFilePath, int algorithmOption, boolean useSorting, boolean shuffle, boolean usePartialCol) {
        Timetable timetable = loadTimetable(xmlFilePath);
        return intervalColoringTwoStepAlgorithm(timetable, algorithmOption, useSorting, shuffle, usePartialCol);
    }

    public static Map<TimetableNode, ColorDayTimeWrap> intervalColoringTwoStepFileContent(
            byte[] xmlFileContent, int algorithmOption, boolean useSorting, boolean shuffle, boolean usePartialCol) {
        Timetable timetable = loadTimetable(xmlFileContent);
        return intervalColoringTwoStepAlgorithm(timetable, algorithmOption, useSorting, shuffle, usePartialCol);
    }

    private static Map<TimetableNode, ColorDayTimeWrap> intervalColoringTwoStepAlgorithm(
            Timetable timetable, int algorithmOption, boolean useSorting, boolean shuffle, boolean usePartialCol) {
        Timetable newTimetable = modelTimetableData(timetable);
        TimeslotDataModel dataModel = new TimeslotDataModel(newTimetable);
        TimeslotColoringSolver coloringSolver = new TimeslotColoringSolver(dataModel);
        coloringSolver.solve(algorithmOption, useSorting, shuffle);
        Map<Timeslot, Set<TimeslotDataNode>> solution = coloringSolver.getTimeslotToNodes();

        if (usePartialCol) {
            Set<TimeslotDataNode> uncoloredNodes = new HashSet<>(coloringSolver.getUncoloredNodes());
            PartialColAlgorithm partialCol = new PartialColAlgorithm(dataModel, coloringSolver.getTimeslotToNodes(),
                                    uncoloredNodes, coloringSolver.getGraph(), 1000);
            partialCol.solve();
            solution = partialCol.getBestSolution();
        }

        Map<Timeslot, Map<TimeslotDataNode, RoomDataNode>> timeslotToSolution = new HashMap<>();
        for (Timeslot timeslot : solution.keySet()) {
            Set<TimeslotDataNode> currentSet = solution.get(timeslot);
            RoomDataGraph graph = new RoomDataGraph(dataModel, currentSet);
            RoomBipartiteSolver bipartiteSolver = new RoomBipartiteSolver(graph);
            bipartiteSolver.hopcroftKarpSolver();
            timeslotToSolution.put(timeslot, bipartiteSolver.getEventPairs());
        }

        return getNodeToColorMap(timeslotToSolution);
    }

    private static Timetable modelTimetableData(Timetable timetable) {
        List<Event> eventsToKeep = new ArrayList<>();
        for (Event event : timetable.getEvents()) {
            if (event.getType().equals("C") || event.getType().equals("L") ||
                    event.getType().equals("S")) {
                eventsToKeep.add(event);
            }
        }
        List<Resource> resourcesToKeep = new ArrayList<>();
        for (Resource resource : timetable.getResources()) {
            if (resource.getCapacity() > 0 && (resource.getType().equals("curs") || resource.getType().equals("lab") ||
                    resource.getType().equals("sem"))) {
                resourcesToKeep.add(resource);
            }
        }
        Events events = new Events(eventsToKeep);
        Resources resources = new Resources(resourcesToKeep);
        timetable.setEvents(events);
        timetable.setResources(resources);
        return timetable;
    }

    private static Map<TimetableNode, ColorDayTimeWrap> getNodeToColorMap(
            Map<Timeslot, Map<TimeslotDataNode, RoomDataNode>> timeslotToSolution) {
        Map<TimetableNode, ColorDayTimeWrap> nodeToColorMap = new HashMap<>();
        for (Timeslot timeslot : timeslotToSolution.keySet()) {
            Map<TimeslotDataNode, RoomDataNode> currentMap = timeslotToSolution.get(timeslot);
            for (TimeslotDataNode timeslotDataNode : currentMap.keySet()) {
                if (timeslotDataNode == null) {
                    continue;
                }
                RoomDataNode roomDataNode = currentMap.get(timeslotDataNode);
                if (roomDataNode == null) {
                    continue;
                }

                TimetableNode timetableNode = new TimetableNode(timeslotDataNode.getEvent(), false, null);
                ColorDayTimeWrap colorDayTimeWrap = new ColorDayTimeWrap(
                        new TimetableColorRoom(roomDataNode.getResource()), timeslot.getDay(),
                        timeslot.getStartTime().getHour());
                nodeToColorMap.put(timetableNode, colorDayTimeWrap);
            }
        }
        return nodeToColorMap;
    }

    public static void setAlgorithmConstants(int numberOfDays, LocalTime startTime, LocalTime endTime,
                                             int generalDuration) {
        AlgorithmConstants.NUMBER_OF_DAYS = numberOfDays;
        AlgorithmConstants.START_TIME = startTime;
        AlgorithmConstants.END_TIME = endTime;
        AlgorithmConstants.GENERAL_DURATION = generalDuration;
    }

    public byte[] getXmlData() {
        File file = new File(XML_FILEPATH);
        try {
            return Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
                     4. Interval coloring two step (Greedy + Hopcroft-Karp)
                     5. Interval coloring two step modified DSatur
                     """);
        int algorithmOption = 0;
        try {
            algorithmOption = scanner.nextInt();
        } catch (Exception e) {
            System.out.println("Invalid input.");
            e.printStackTrace();
        }

        if (algorithmOption == 1) {
            roomOnlyColoringFilePath(XML_FILEPATH, 1, false, false);
        } else if (algorithmOption == 2) {
            intervalRoomColoringFilePath(XML_FILEPATH, 1, false, false);
        } else if (algorithmOption == 3) {
            intervalRoomColoringFilePath(XML_FILEPATH, 2, false, false);
        } else if (algorithmOption == 4) {
            intervalColoringTwoStepFilePath(XML_FILEPATH, 1, false, false, false);
        } else if (algorithmOption == 5) {
            intervalColoringTwoStepFilePath(XML_FILEPATH, 2, false, false, true);
        } else {
            System.out.println("Invalid input.");
        }
    }
}