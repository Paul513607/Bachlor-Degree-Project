package org.timetable.algorithm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.timetable.model.*;
import org.timetable.pojo.Group;

import java.awt.*;
import java.time.LocalTime;
import java.util.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IntervalRoomColoringAlgorithm extends TimetableColoringAlgorithm {
    private List<TimetableColorIntervalRoom> timetableLaboratoryColors = new ArrayList<>();
    private List<TimetableColorIntervalRoom> timetableCourseColors = new ArrayList<>();
    private Map<TimetableNode, ColorDayTimeWrap> nodeColorMap = new HashMap<>();

    public int algorithmOption = 1;

    public IntervalRoomColoringAlgorithm(TimetableGraph<TimetableNode, TimetableEdge> graph,
                                         List<TimetableColorIntervalRoom> timetableLaboratoryColors,
                                         List<TimetableColorIntervalRoom> timetableCourseColors) {
        super(graph);
        this.timetableLaboratoryColors = timetableLaboratoryColors;
        this.timetableCourseColors = timetableCourseColors;
    }

    public IntervalRoomColoringAlgorithm(TimetableGraph<TimetableNode, TimetableEdge> graph,
                                         List<TimetableColorIntervalRoom> timetableLaboratoryColors,
                                         List<TimetableColorIntervalRoom> timetableCourseColors,
                                         int algorithmOption) {
        super(graph);
        this.timetableLaboratoryColors = timetableLaboratoryColors;
        this.timetableCourseColors = timetableCourseColors;
        this.algorithmOption = 1;
    }

    @Override
    public void colorGraph() {
        sortTimetableNodesByEdgeCount();

        if (algorithmOption == 1) {
            greedyColoringMethod();
        } else if (algorithmOption == 2) {
            dsaturColoringMethod();
        }
    }

    public void greedyColoringMethod() {
        Set<TimetableColorIntervalRoom> availableLaboratoryColors = new HashSet<>(timetableLaboratoryColors);
        Set<TimetableColorIntervalRoom> availableCourseColors = new HashSet<>(timetableCourseColors);

        for (TimetableNode node : graph.getNodes()) {
            if (canExcludeEvent(node)) {
                continue;
            }

            Set<TimetableNode> neighbours = graph.getNeighbours(node);

            Set<TimetableColorIntervalRoom> usedColors = new HashSet<>();
            Set<TimetableColorIntervalRoom> availableColors;
            if (node.getEvent().getType().equals("C")) {
                availableColors = new HashSet<>(availableCourseColors);
            } else {
                availableColors = new HashSet<>(availableLaboratoryColors);
            }

            for (TimetableNode neighbour : neighbours) {
                if (nodeColorMap.containsKey(neighbour)) {
                    ColorDayTimeWrap wrap = nodeColorMap.get(neighbour);
                    usedColors.add(new TimetableColorIntervalRoom(wrap.getDay(), LocalTime.of(wrap.getTime(), 0),
                            wrap.getColor().getResource()));
                }
            }

            availableColors.removeAll(usedColors);
            if (availableColors.isEmpty()) {
                continue;
            }

            TimetableColorIntervalRoom firstColor = availableColors.iterator().next();
            nodeColorMap.put(node, new ColorDayTimeWrap(new TimetableColorRoom(firstColor.getResource()),
                                            firstColor.getDay(), firstColor.getTime().getHour()));
        }
    }

    public void dsaturColoringMethod() {

    }

    public boolean canExcludeEvent(TimetableNode node) {
        // Add more exclusion parameters
        int totalMemberCount = node.getEvent().getGroupListNoParents().stream()
                .mapToInt(Group::getMemberCount)
                .sum();
        return totalMemberCount == 0;
    }
}
