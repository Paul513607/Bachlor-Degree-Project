package org.timetable.algorithm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.timetable.algorithm.wraps.ColorDayTimeWrap;
import org.timetable.algorithm.wraps.NodeSaturationComparator;
import org.timetable.algorithm.wraps.TimetableNodeSatur;
import org.timetable.model.*;
import org.timetable.pojo.Group;

import java.time.LocalTime;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

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
        this.algorithmOption = algorithmOption;
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

            int totalMemberCount = node.getEvent().getGroupListNoParents().stream()
                    .mapToInt(Group::getMemberCount)
                    .sum() / 4;

            availableColors.removeAll(usedColors);
            availableColors = availableColors.stream()
                    .filter(color -> color.getResource().getCapacity() >= totalMemberCount)
                    .collect(Collectors.toSet());
            if (availableColors.isEmpty()) {
                continue;
            }

            TimetableColorIntervalRoom firstColor = availableColors.iterator().next();
            nodeColorMap.put(node, new ColorDayTimeWrap(new TimetableColorRoom(firstColor.getResource()),
                                            firstColor.getDay(), firstColor.getTime().getHour()));
        }
    }

    public void dsaturColoringMethod() {
        Set<TimetableColorIntervalRoom> availableLaboratoryColors = new HashSet<>(timetableLaboratoryColors);
        Set<TimetableColorIntervalRoom> availableCourseColors = new HashSet<>(timetableCourseColors);

        Set<TimetableNodeSatur> nodeSaturList = new HashSet<>();
        for (TimetableNode node : graph.getNodes()) {
            int degree = (int) graph.getEdges().stream()
                    .filter(edge -> edge.containsNode(node)).count();
            nodeSaturList.add(new TimetableNodeSatur(0, degree, node));
        }

        Queue<TimetableNodeSatur> queue =
                new PriorityQueue<>(graph.getNodes().size(), NodeSaturationComparator.getInstance());

        queue.addAll(nodeSaturList);
        while (!queue.isEmpty()) {
            TimetableNode node = queue.poll().getNode();

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

            int totalMemberCount = node.getEvent().getGroupListNoParents().stream()
                    .mapToInt(Group::getMemberCount)
                    .sum() / 4;
            availableColors.removeAll(usedColors);
            availableColors = availableColors.stream()
                    .filter(color -> color.getResource().getCapacity() >= totalMemberCount)
                    .collect(Collectors.toSet());
            if (availableColors.isEmpty()) {
                continue;
            }

            TimetableColorIntervalRoom firstColor = availableColors.iterator().next();
            nodeColorMap.put(node, new ColorDayTimeWrap(new TimetableColorRoom(firstColor.getResource()),
                    firstColor.getDay(), firstColor.getTime().getHour()));

            for (TimetableNode neighbour : neighbours) {
                if (nodeColorMap.containsKey(neighbour)) {
                    continue;
                }
                Optional<TimetableNodeSatur> nodeSaturOpt = nodeSaturList.stream()
                        .filter(saturNode -> saturNode.getNode().equals(neighbour))
                        .findFirst();

                if (nodeSaturOpt.isEmpty()) {
                    continue;
                }
                TimetableNodeSatur nodeSatur = nodeSaturOpt.get();
                queue.remove(nodeSatur);
                nodeSatur.setSaturation(nodeSatur.getSaturation() + 1);
                queue.add(nodeSatur);
            }
        }
    }

    public boolean canExcludeEvent(TimetableNode node) {
        // Add more exclusion parameters
        int totalMemberCount = node.getEvent().getGroupListNoParents().stream()
                .mapToInt(Group::getMemberCount)
                .sum();
        return totalMemberCount == 0;
    }

    public void sortNodeColorMapByActorsAndDayTime() {
        List<Map.Entry<TimetableNode, ColorDayTimeWrap>> list = new ArrayList<>(nodeColorMap.entrySet());
        list.sort((o1, o2) -> {
            String o1FirstGroup = o1.getKey().getEvent().getGroupList().get(0).getAbbr();
            String o2FirstGroup = o2.getKey().getEvent().getGroupList().get(0).getAbbr();
            int compare = o1FirstGroup.compareTo(o2FirstGroup);
            if (compare != 0) {
                return compare;
            }
            int o1Day = o1.getValue().getDay();
            int o2Day = o2.getValue().getDay();
            compare = Integer.compare(o1Day, o2Day);
            if (compare != 0) {
                return compare;
            }
            int o1Time = o1.getValue().getTime();
            int o2Time = o2.getValue().getTime();
            return Integer.compare(o1Time, o2Time);
        });
    }

    public void printNodeColorMap() {
        for (Map.Entry<TimetableNode, ColorDayTimeWrap> entry : nodeColorMap.entrySet()) {
            System.out.println(entry.getKey().getEvent() + "\n" + entry.getValue() + "\n");
        }
    }
}
