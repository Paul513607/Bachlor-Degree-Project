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
import org.timetable.util.Timer;

import java.io.*;
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

            PartialColAlgorithm partialCol = new PartialColAlgorithm(dataModel, solution,
                                    uncoloredNodes, coloringSolver.getGraph(), 1000);
            partialCol.solve();
            solution = partialCol.getBestIterationSolution();
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

    private static void checkUnassignedEvents(Map<TimetableNode, ColorDayTimeWrap> solution, Timetable timetable) {
        Set<Event> unassignedEventsSet = new HashSet<>();
        for (Event event : timetable.getEvents()) {
            if (event.getType().equals("C") || event.getType().equals("L") ||
                    event.getType().equals("S")) {
                unassignedEventsSet.add(event);
            }
        }
        for (TimetableNode node : solution.keySet()) {
            unassignedEventsSet.remove(node.getEvent());
        }

        System.out.println("Unassigned events: " + unassignedEventsSet.size());
        System.out.println("Unassigned courses: " + unassignedEventsSet.stream()
                .filter(e -> e.getType().equals("C")).count());
        System.out.println("Unassigned seminars/labs: " + unassignedEventsSet.stream()
                .filter(e -> e.getType().equals("L") || e.getType().equals("S")).count());
    }

    private static void checkUnassignedEvents(Map<TimetableNode, ColorDayTimeWrap> solution, Timetable timetable,
                                             List<Long> unassignedEvents, List<Long> unassignedCourses,
                                             List<Long> unassignedSeminarsLabs) {
        Set<Event> unassignedEventsSet = new HashSet<>();
        for (Event event : timetable.getEvents()) {
            if (event.getType().equals("C") || event.getType().equals("L") ||
                    event.getType().equals("S")) {
                unassignedEventsSet.add(event);
            }
        }
        for (TimetableNode node : solution.keySet()) {
            unassignedEventsSet.remove(node.getEvent());
        }

        unassignedEvents.add((long) unassignedEventsSet.size());
        unassignedCourses.add(unassignedEventsSet.stream().filter(e -> e.getType().equals("C")).count());
        unassignedSeminarsLabs.add(unassignedEventsSet.stream().filter(e -> e.getType().equals("S") || e.getType().equals("L")).count());
    }

    private static long checkNumberOfCollisions(Map<TimetableNode, ColorDayTimeWrap> solution) {
        long numberOfCollisions = 0;
        List<TimetableNode> list1 = new ArrayList<>(solution.keySet());
        List<TimetableNode> list2 = new ArrayList<>(solution.keySet());
        for (int i = 0; i < list1.size(); i++) {
            TimetableNode node1 = list1.get(i);
            for (int j = i + 1; j < list2.size(); j++) {
                TimetableNode node2 = list2.get(j);

                ColorDayTimeWrap wrap1 = solution.get(node1);
                ColorDayTimeWrap wrap2 = solution.get(node2);

                if (wrap1.getDay() == wrap2.getDay() && wrap1.getTime() == wrap2.getTime()) {
                    if (wrap1.getColor().getResource().equals(wrap2.getColor().getResource())) {
                        numberOfCollisions++;
                        continue;
                    }

                    List<Group> studentGroupList1 = node1.getEvent().getGroupList();
                    List<Group> studentGroupList2 = node2.getEvent().getGroupList();
                    List<Group> intersection = new ArrayList<>(studentGroupList1);
                    intersection.retainAll(studentGroupList2);

                    if (!intersection.isEmpty()) {
                        numberOfCollisions++;
                        continue;
                    }

                    List<Prof> profList1 = node1.getEvent().getProfList();
                    List<Prof> profList2 = node2.getEvent().getProfList();
                    List<Prof> intersection2 = new ArrayList<>(profList1);
                    intersection2.retainAll(profList2);

                    if (!intersection2.isEmpty()) {
                        numberOfCollisions++;
                    }
                }
            }
        }
        return numberOfCollisions;
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

        Map<TimetableNode, ColorDayTimeWrap> solution = null;
        if (algorithmOption == 1) {
            solution = roomOnlyColoringFilePath(XML_FILEPATH, 1, false, false);
        } else if (algorithmOption == 2) {
            solution = intervalRoomColoringFilePath(XML_FILEPATH, 1, false, false);
        } else if (algorithmOption == 3) {
            solution = intervalRoomColoringFilePath(XML_FILEPATH, 2, false, false);
        } else if (algorithmOption == 4) {
            solution = intervalColoringTwoStepFilePath(XML_FILEPATH, 1, false, false, false);
        } else if (algorithmOption == 5) {
            solution = intervalColoringTwoStepFilePath(XML_FILEPATH, 2, false, false, true);
        } else {
            System.out.println("Invalid input.");
        }

        // run30timesEachAndAverage();
    }

    private static Map<TimetableNode, ColorDayTimeWrap> runAlgorithmWithName(String name, boolean useSorting, boolean shuffle) {
        return switch (name) {
            case "Room only coloring" -> roomOnlyColoringFilePath(XML_FILEPATH, 1, useSorting, shuffle);
            case "Interval room coloring - Greedy" ->
                    intervalRoomColoringFilePath(XML_FILEPATH, 1, useSorting, shuffle);
            case "Interval room coloring - DSatur" ->
                    intervalRoomColoringFilePath(XML_FILEPATH, 2, useSorting, shuffle);
            case "Interval coloring two step (Greedy + Hopcroft-Karp)" ->
                    intervalColoringTwoStepFilePath(XML_FILEPATH, 1, useSorting, shuffle, false);
            case "Interval coloring two step modified DSatur" ->
                    intervalColoringTwoStepFilePath(XML_FILEPATH, 2, useSorting, shuffle, false);
            case "Interval coloring two step (Greedy + Hopcroft-Karp) + PartialCol" ->
                    intervalColoringTwoStepFilePath(XML_FILEPATH, 1, useSorting, shuffle, true);
            case "Interval coloring two step modified DSatur + PartialCol" ->
                    intervalColoringTwoStepFilePath(XML_FILEPATH, 2, useSorting, shuffle, true);
            default -> null;
        };
    }

    public static void run30timesEachAndAverage() {
        Map<String, List<Long>> timeBenchmarks = new HashMap<>();
        Map<String, List<Long>> unassignedEventsBenchmarks = new HashMap<>();
        Map<String, List<Long>> unassignedCoursesBenchmarks = new HashMap<>();
        Map<String, List<Long>> unassignedSeminarsLabsBenchmarks = new HashMap<>();
        Map<String, List<Long>> numberOfCollisionsBenchmarks = new HashMap<>();

        List<String> algorithmNames = new ArrayList<>();
        algorithmNames.add("Room only coloring");
        algorithmNames.add("Interval room coloring - Greedy");
        algorithmNames.add("Interval room coloring - DSatur");
        algorithmNames.add("Interval coloring two step (Greedy + Hopcroft-Karp)");
        algorithmNames.add("Interval coloring two step modified DSatur");
        algorithmNames.add("Interval coloring two step (Greedy + Hopcroft-Karp) + PartialCol");
        algorithmNames.add("Interval coloring two step modified DSatur + PartialCol");

        for (String algorithmName : algorithmNames) {
            timeBenchmarks.put(algorithmName, new ArrayList<>());
            unassignedEventsBenchmarks.put(algorithmName, new ArrayList<>());
            unassignedCoursesBenchmarks.put(algorithmName, new ArrayList<>());
            unassignedSeminarsLabsBenchmarks.put(algorithmName, new ArrayList<>());
            numberOfCollisionsBenchmarks.put(algorithmName, new ArrayList<>());
        }

        Timetable timetable = loadTimetable(XML_FILEPATH);
        timetable = modelTimetableData(timetable);
        Timer timer = Timer.getInstance();
        // 10 with useSorting = false and shuffle = false
        for (int i = 0; i < 10; i++) {
            System.out.println("Iteration: " + i);
            for (String algorithmName : algorithmNames) {
                timer.start();
                Map<TimetableNode, ColorDayTimeWrap> solution = runAlgorithmWithName(algorithmName, false, false);
                timer.end();

                timeBenchmarks.get(algorithmName).add(timer.getDuration());

                checkUnassignedEvents(solution, timetable, unassignedEventsBenchmarks.get(algorithmName),
                                      unassignedCoursesBenchmarks.get(algorithmName),
                                      unassignedSeminarsLabsBenchmarks.get(algorithmName));
                numberOfCollisionsBenchmarks.get(algorithmName).add(checkNumberOfCollisions(solution));
            }
        }

        // 10 with useSorting = true and shuffle = false
        for (int i = 0; i < 10; i++) {
            System.out.println("Iteration: " + i);
            for (String algorithmName : algorithmNames) {
                timer.start();
                Map<TimetableNode, ColorDayTimeWrap> solution = runAlgorithmWithName(algorithmName, true, false);
                timer.end();

                timeBenchmarks.get(algorithmName).add(timer.getDuration());

                checkUnassignedEvents(solution, timetable, unassignedEventsBenchmarks.get(algorithmName),
                                      unassignedCoursesBenchmarks.get(algorithmName),
                                      unassignedSeminarsLabsBenchmarks.get(algorithmName));
                numberOfCollisionsBenchmarks.get(algorithmName).add(checkNumberOfCollisions(solution));
            }
        }

        // 10 with useSorting = false and shuffle = true
        for (int i = 0; i < 10; i++) {
            System.out.println("Iteration: " + i);
            for (String algorithmName : algorithmNames) {
                timer.start();
                Map<TimetableNode, ColorDayTimeWrap> solution = runAlgorithmWithName(algorithmName, false, true);
                timer.end();

                timeBenchmarks.get(algorithmName).add(timer.getDuration());

                checkUnassignedEvents(solution, timetable, unassignedEventsBenchmarks.get(algorithmName),
                                      unassignedCoursesBenchmarks.get(algorithmName),
                                      unassignedSeminarsLabsBenchmarks.get(algorithmName));
                numberOfCollisionsBenchmarks.get(algorithmName).add(checkNumberOfCollisions(solution));
            }
        }

        // save results in 3 csv files
        try (FileWriter fileWriter = new FileWriter("tt-no-params.csv")) {
            fileWriter.append("Algorithm name,Time,Unassigned events,Unassigned courses,Unassigned seminars/labs, Number of collisions\n");
            for (String algorithmName : algorithmNames) {
                for (int i = 0; i < 10; i++) {
                    fileWriter.append(algorithmName).append(",");
                    fileWriter.append(timeBenchmarks.get(algorithmName).get(i).toString()).append(",");
                    fileWriter.append(unassignedEventsBenchmarks.get(algorithmName).get(i).toString()).append(",");
                    fileWriter.append(unassignedCoursesBenchmarks.get(algorithmName).get(i).toString()).append(",");
                    fileWriter.append(unassignedSeminarsLabsBenchmarks.get(algorithmName).get(i).toString()).append(",");
                    fileWriter.append(numberOfCollisionsBenchmarks.get(algorithmName).get(i).toString()).append("\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (FileWriter fileWriter = new FileWriter("tt-use-sorting.csv")) {
            fileWriter.append("Algorithm name,Time,Unassigned events,Unassigned courses,Unassigned seminars/labs,Number of collisions\n");
            for (String algorithmName : algorithmNames) {
                for (int i = 10; i < 20; i++) {
                    fileWriter.append(algorithmName).append(",");
                    fileWriter.append(timeBenchmarks.get(algorithmName).get(i).toString()).append(",");
                    fileWriter.append(unassignedEventsBenchmarks.get(algorithmName).get(i).toString()).append(",");
                    fileWriter.append(unassignedCoursesBenchmarks.get(algorithmName).get(i).toString()).append(",");
                    fileWriter.append(unassignedSeminarsLabsBenchmarks.get(algorithmName).get(i).toString()).append(",");
                    fileWriter.append(numberOfCollisionsBenchmarks.get(algorithmName).get(i).toString()).append("\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (FileWriter fileWriter = new FileWriter("tt-shuffle.csv")) {
            fileWriter.append("Algorithm name,Time,Unassigned events,Unassigned courses,Unassigned seminars/labs, Number of collisions\n");
            for (String algorithmName : algorithmNames) {
                for (int i = 20; i < 30; i++) {
                    fileWriter.append(algorithmName).append(",");
                    fileWriter.append(timeBenchmarks.get(algorithmName).get(i).toString()).append(",");
                    fileWriter.append(unassignedEventsBenchmarks.get(algorithmName).get(i).toString()).append(",");
                    fileWriter.append(unassignedCoursesBenchmarks.get(algorithmName).get(i).toString()).append(",");
                    fileWriter.append(unassignedSeminarsLabsBenchmarks.get(algorithmName).get(i).toString()).append(",");
                    fileWriter.append(numberOfCollisionsBenchmarks.get(algorithmName).get(i).toString()).append("\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}