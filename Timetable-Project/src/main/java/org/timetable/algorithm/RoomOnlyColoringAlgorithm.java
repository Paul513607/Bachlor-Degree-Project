package org.timetable.algorithm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.timetable.algorithm.wraps.AlgorithmConstants;
import org.timetable.algorithm.wraps.ColorDayTimeWrap;
import org.timetable.algorithm.wraps.GroupDayTimeWrap;
import org.timetable.model.TimetableColorRoom;
import org.timetable.model.TimetableEdge;
import org.timetable.model.TimetableGraph;
import org.timetable.model.TimetableNode;
import org.timetable.pojo.Group;

import java.time.LocalTime;
import java.util.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomOnlyColoringAlgorithm extends TimetableColoringAlgorithm {
    private static final Integer MAX_SUCCESSIVE_ASSIGNMENTS = 3;
    private List<TimetableColorRoom> timetableLaboratoryColors = new ArrayList<>();
    private List<TimetableColorRoom> timetableCourseColors = new ArrayList<>();

    private Map<GroupDayTimeWrap, TimetableColorRoom> groupColorMap = new HashMap<>();

    public RoomOnlyColoringAlgorithm(TimetableGraph<TimetableNode, TimetableEdge> graph,
                                     List<TimetableColorRoom> timetableLaboratoryColors,
                                     List<TimetableColorRoom> timetableCourseColors) {
        super(graph);
        this.timetableLaboratoryColors = timetableLaboratoryColors;
        this.timetableCourseColors = timetableCourseColors;
    }

    @Override
    public void colorGraph() {
        colorGraph(1, false, false);
    }

    public void applyOptimisations(boolean useSorting, boolean shuffle) {
        // if useSorting is true, ignore shuffle
        if (useSorting) {
            sortTimetableNodesByEdgeCount();
        } else if (shuffle) {
            Collections.shuffle(graph.getNodes());
        }
    }

    public void colorGraph(int algorithmOption, boolean useSorting, boolean shuffle) {
        applyOptimisations(useSorting, shuffle);
        for (int day = 0; day < AlgorithmConstants.NUMBER_OF_DAYS; day++) {
            for (LocalTime time = AlgorithmConstants.START_TIME; time.isBefore(AlgorithmConstants.END_TIME);
                 time = time.plusHours(AlgorithmConstants.GENERAL_DURATION)) {
                colorNodesAtDayAndTime(day, time);
            }
        }
    }

    private List<TimetableNode> filterNodesByMaxSuccessiveAssignments(List<TimetableNode> prevAssignedNodes,
                                                                      int day, LocalTime time) {
        Set<Group> prevGroups = new HashSet<>();
        List<TimetableNode> filteredNodes = new ArrayList<>();
        for (TimetableNode node : prevAssignedNodes) {
            List<Group> groups = node.getEvent().getGroupListNoParents();
            prevGroups.addAll(groups);
        }

        Map<Group, Boolean> canAssignMap = new HashMap<>();
        for (Group group : prevGroups) {
            int successiveAssignments = 0;
            for (GroupDayTimeWrap groupDayTimeWrap : groupColorMap.keySet()) {
                if (groupDayTimeWrap.getDay() == day && groupDayTimeWrap.getGroupList().contains(group)) {
                    successiveAssignments++;
                }
            }
            if (successiveAssignments < MAX_SUCCESSIVE_ASSIGNMENTS) {
                canAssignMap.put(group, true);
            } else {
                canAssignMap.put(group, false);
            }
        }

        for (TimetableNode node : prevAssignedNodes) {
            List<Group> groups = node.getEvent().getGroupListNoParents();
            groups.retainAll(prevGroups);
            int canAssignCount = 0;
            for (Group group : groups) {
                if (canAssignMap.get(group)) {
                    canAssignCount++;
                }
            }

            if (canAssignCount == groups.size()) {
                filteredNodes.add(node);
            }
        }

        return filteredNodes;
    }

    private void colorNodesAtDayAndTime(int day, LocalTime time) {
        Set<TimetableColorRoom> availableColorsLab = new HashSet<>(timetableLaboratoryColors);
        Set<TimetableColorRoom> availableColorsCourse = new HashSet<>(timetableCourseColors);
        Set<Group> assignedGroups = new HashSet<>();
        // Try to assign rooms to groups that have already been assigned
        List<TimetableNode> previouslyAssignedNodes = getPreviouslyAssignedNodes(day, time);
        previouslyAssignedNodes = filterNodesByMaxSuccessiveAssignments(previouslyAssignedNodes, day, time);
        List<TimetableNode> otherNodes = new ArrayList<>(graph.getNodes());
        otherNodes.removeAll(previouslyAssignedNodes);

        assignedGroups = assignColorToNode(day, time, availableColorsLab, availableColorsCourse,
                assignedGroups, previouslyAssignedNodes);
        assignedGroups = assignColorToNode(day, time, availableColorsLab, availableColorsCourse,
                assignedGroups, otherNodes);
    }

    private Set<Group> assignColorToNode(int day, LocalTime time,
                                         Set<TimetableColorRoom> availableColorsLaboratory,
                                         Set<TimetableColorRoom> availableColorsCourse,
                                         Set<Group> assignedGroups, List<TimetableNode> nodes) {
        for (TimetableNode node : nodes) {
            if (node.isAssigned() || canExcludeEvent(node)) {
                continue;
            }
            Set<Group> tmpGroups = new HashSet<>(assignedGroups);
            tmpGroups.retainAll(node.getEvent().getGroupList());
            if (!tmpGroups.isEmpty()) {
                continue;
            }

            Optional<TimetableColorRoom> colorOpt = getAvailableColor(node, day, time,
                    availableColorsLaboratory, availableColorsCourse);
            if (colorOpt.isEmpty()) {
                continue;
            }
            TimetableColorRoom color = colorOpt.get();
            node.setAssigned(true);
            node.setColor(color);
            ColorDayTimeWrap wrap = new ColorDayTimeWrap(color, day, time.getHour());
            nodeColorMap.put(node, wrap);

            GroupDayTimeWrap groupDayTimeWrap = new GroupDayTimeWrap(node.getEvent().getGroupList(), day, time.getHour());
            groupColorMap.put(groupDayTimeWrap, color);
            assignedGroups.addAll(node.getEvent().getGroupList());
        }
        return assignedGroups;
    }

    private List<TimetableNode> getPreviouslyAssignedNodes(int day, LocalTime time) {
        List<TimetableNode> nodes = new ArrayList<>();
        for (TimetableNode node : graph.getNodes()) {
            List<Group> groups = node.getEvent().getGroupList();
            GroupDayTimeWrap wrap = new GroupDayTimeWrap(groups, day,
                    time.minusHours(AlgorithmConstants.GENERAL_DURATION).getHour());
            if (groupColorMap.containsKey(wrap)) {
                nodes.add(node);
            }
        }
        return nodes;
    }

    private boolean areProfsAvailable(TimetableNode node, int day, LocalTime time) {
        for (Map.Entry<TimetableNode, ColorDayTimeWrap> entry : nodeColorMap.entrySet()) {
            if (entry.getKey() == node) {
                continue;
            }
            if (entry.getValue().getDay() == day && entry.getValue().getTime() == time.getHour()) {
                if (graph.containsEdge(node, entry.getKey())) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isRoomFree(TimetableNode node, int day, LocalTime time, TimetableColorRoom color) {
        for (Map.Entry<TimetableNode, ColorDayTimeWrap> entry : nodeColorMap.entrySet()) {
            if (entry.getKey() == node) {
                continue;
            }
            if (entry.getValue().getDay() == day && entry.getValue().getTime() == time.getHour()) {
                if (entry.getValue().getColor().equals(color)) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean hasCapacity(TimetableNode node, TimetableColorRoom color) {
        int totalMemberCount = node.getEvent().getGroupListNoParents().stream()
                .mapToInt(Group::getMemberCount)
                .sum();
        return totalMemberCount <= color.getResource().getCapacity();
    }

    public boolean canExcludeEvent(TimetableNode node) {
        // Add more exclusion parameters
        int totalMemberCount = node.getEvent().getGroupListNoParents().stream()
                .mapToInt(Group::getMemberCount)
                .sum();
        return totalMemberCount == 0;
    }

    private boolean isColorValid(TimetableNode node, int day, LocalTime time, TimetableColorRoom color) {
        return !nodeColorMap.containsKey(node) && areProfsAvailable(node, day, time) &&
                isRoomFree(node, day, time, color) && hasCapacity(node, color);
    }

    private Optional<TimetableColorRoom> getAvailableColor(TimetableNode node, int day, LocalTime time,
                                                           Set<TimetableColorRoom> availableColorsLaboratory,
                                                           Set<TimetableColorRoom> availableColorsCourse) {
        GroupDayTimeWrap wrap = new GroupDayTimeWrap(node.getEvent().getGroupList(),
                day, time.minusHours(AlgorithmConstants.GENERAL_DURATION).getHour());
        if (groupColorMap.containsKey(wrap)) {
            TimetableColorRoom color = groupColorMap.get(wrap);
            if (isColorValid(node, day, time, color)) {
                return Optional.of(color);
            }
        }

        Set<TimetableColorRoom> availableColors = new HashSet<>();
        if (node.getEvent().getType().equals("C")) {
            availableColors = availableColorsCourse;
        } else {
            availableColors = availableColorsLaboratory;
        }

        for (TimetableColorRoom color : availableColors) {
            if (isColorValid(node, day, time, color)) {
                return Optional.of(color);
            }
        }
        return Optional.empty();
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
