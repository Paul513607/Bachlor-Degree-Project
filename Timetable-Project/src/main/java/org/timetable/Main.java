package org.timetable;

import org.timetable.algorithm.IntervalRoomColoringAlgorithm;
import org.timetable.algorithm.RoomOnlyColoringAlgorithm;
import org.timetable.graph_generators.GraphGenerator;
import org.timetable.graph_generators.IntervalRoomColorGraphGenerator;
import org.timetable.graph_generators.RoomColorGraphGenerator;
import org.timetable.model.*;
import org.timetable.pojo.*;
import org.timetable.util.Parser;

import java.io.IOException;
import java.util.List;

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

    public static void main(String[] args) {
        Main app = new Main();

        int test = 3;
        if (test == 1) {
            RoomColorGraphGenerator generator = new RoomColorGraphGenerator();
            Timetable timetable = app.loadTimetable();
            TimetableGraph<TimetableNode, TimetableEdge> graph = generator.createGraph(timetable);
            List<TimetableColorRoom> laboratoryColors = generator.createTimetableLaboratoryColors(timetable);
            List<TimetableColorRoom> courseColors = generator.createTimetableCourseColors(timetable);

            RoomOnlyColoringAlgorithm algorithm =
                    new RoomOnlyColoringAlgorithm(graph, laboratoryColors, courseColors);
            algorithm.colorGraph();
            algorithm.sortNodeColorMapByActorsAndDayTimeAndPrint();
        } else if (test == 2 || test == 3) {
            IntervalRoomColorGraphGenerator generator = new IntervalRoomColorGraphGenerator();
            Timetable timetable = app.loadTimetable();
            TimetableGraph<TimetableNode, TimetableEdge> graph = generator.createGraph(timetable);
            List<TimetableColorIntervalRoom> laboratoryColors = generator.createTimetableLaboratoryColors(timetable);
            List<TimetableColorIntervalRoom> courseColors = generator.createTimetableCourseColors(timetable);

            IntervalRoomColoringAlgorithm algorithm =
                    new IntervalRoomColoringAlgorithm(graph, laboratoryColors, courseColors, test);
            algorithm.colorGraph();
            algorithm.sortNodeColorMapByActorsAndDayTimeAndPrint();
        }
    }
}