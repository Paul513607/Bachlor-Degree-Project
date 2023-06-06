package org.timetable.algorithm.interval_then_room;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.timetable.algorithm.interval_then_room.datamodel.TimeslotDataModel;
import org.timetable.algorithm.interval_then_room.model.Timeslot;
import org.timetable.algorithm.interval_then_room.model.TimeslotDataGraph;
import org.timetable.algorithm.interval_then_room.model.TimeslotDataNode;
import org.timetable.generic_model.TimetableNode;

import java.util.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimeslotColoringSolver {
    private TimeslotDataGraph graph;
    private List<Timeslot> colorList = new ArrayList<>();
    private TimeslotDataModel model;
    private Map<TimeslotDataNode, Timeslot> solution = new HashMap<>();
    private Map<Timeslot, Set<TimeslotDataNode>> timeslotToNodes = new HashMap<>();
    private List<TimeslotDataNode> uncoloredNodes = new ArrayList<>();

    private int algorithmOption = 1;

    public TimeslotColoringSolver(TimeslotDataModel model) {
        graph = new TimeslotDataGraph(model);
        colorList = new ArrayList<>(model.getTimeslots());
        this.model = model;
    }

    private void sortByDegrees(List<Integer> degrees) {
        List<Integer> degreesCopy = new ArrayList<>(degrees);

        int size = graph.getNodes().size();
        List<TimeslotDataNode> nodesSorted = new ArrayList<>(graph.getNodes());
        int[][] adjacencyMatrixSorted = new int[size][size];

        boolean found;
        do {
            found = false;
            for (int i = 0; i < size - 1; i++) {
                if (degreesCopy.get(i) < degreesCopy.get(i + 1)) {
                    Collections.swap(degreesCopy, i, i + 1);
                    Collections.swap(nodesSorted, i, i + 1);
                    found = true;
                }
            }
        } while (found);

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < graph.getNodes().size(); j++) {
                if (graph.getNodes().get(j).equals(nodesSorted.get(i))) {
                    for (int k = 0; k < size; k++) {
                        adjacencyMatrixSorted[i][k] = graph.getAdjacencyMatrix()[j][k];
                    }
                }
            }
        }

        graph.setNodes(nodesSorted);
        graph.setAdjacencyMatrix(adjacencyMatrixSorted);
    }

    private void applyOptimisations(boolean useSorting, boolean shuffle) {
        // if useSorting is true, ignore shuffle
        if (useSorting) {
            List<Integer> degrees = new ArrayList<>();
            for (int i = 0; i < graph.getNodes().size(); i++) {
                int degree = 0;
                for (int j = 0; j < graph.getNodes().size(); j++) {
                    if (graph.getAdjacencyMatrix()[i][j] == 1) {
                        degree++;
                    }
                }
                degrees.add(degree);
            }
            sortByDegrees(degrees);
        } else if (shuffle) {
            List<TimeslotDataNode> nodes = graph.getNodes();
            Collections.shuffle(nodes);
            graph.setNodes(nodes);
        }
    }

    private void formatSolution() {
        for (TimeslotDataNode node : solution.keySet()) {
            Timeslot timeslot = solution.get(node);
            if (!timeslotToNodes.containsKey(timeslot)) {
                timeslotToNodes.put(timeslot, new HashSet<>());
            }
            timeslotToNodes.get(timeslot).add(node);
        }

        for (TimeslotDataNode node : graph.getNodes()) {
            if (!solution.containsKey(node)) {
                uncoloredNodes.add(node);
            }
        }
    }

    public void solve(int algorithmOption, boolean useSorting, boolean shuffle) {
        applyOptimisations(useSorting, shuffle);
        if (algorithmOption == 1) {
            solveGreedy();
        } else if (algorithmOption == 2) {
            solvemodifiedDSatur();
        }

        formatSolution();
    }

    private void solveGreedy() {
        for (TimeslotDataNode node : graph.getNodes()) {
            List<Timeslot> availableColors = new ArrayList<>(colorList);
            for (TimeslotDataNode neighbor : graph.getNeighbors(node)) {
                if (solution.containsKey(neighbor)) {
                    availableColors.remove(solution.get(neighbor));
                }
            }
            if (!availableColors.isEmpty()) {
                solution.put(node, availableColors.get(0));
            }
        }
    }

    private List<TimeslotDataNode> applyHeuristic1(List<TimeslotDataNode> unplacedEvents) {
        List<TimeslotDataNode> chosenEvents = new ArrayList<>();
        int[] availableColorForEvent = new int[unplacedEvents.size()];
        int minAvailableColors = Integer.MAX_VALUE;
        for (int i = 0; i < unplacedEvents.size(); i++) {
            TimeslotDataNode node = unplacedEvents.get(i);
            List<Timeslot> availableColors = new ArrayList<>(colorList);
            for (TimeslotDataNode neighbor : graph.getNeighbors(node)) {
                if (solution.containsKey(neighbor)) {
                    availableColors.remove(solution.get(neighbor));
                }
            }
            if (availableColors.size() < minAvailableColors) {
                minAvailableColors = availableColors.size();
            }
            availableColorForEvent[i] = availableColors.size();
        }

        for (int i = 0; i < unplacedEvents.size(); i++) {
            if (availableColorForEvent[i] == minAvailableColors) {
                chosenEvents.add(unplacedEvents.get(i));
            }
        }
        return chosenEvents;
    }

    private List<TimeslotDataNode> applyHeuristic2(List<TimeslotDataNode> unplacedEvents) {
        List<TimeslotDataNode> chosenEvents = new ArrayList<>();
        int[] amountOfNeighbours = new int[unplacedEvents.size()];
        int maxNeighbors = Integer.MIN_VALUE;
        for (int i = 0; i < unplacedEvents.size(); i++) {
            TimeslotDataNode node = unplacedEvents.get(i);
            int neighborCount = graph.getNeighbors(node).size();
            if (neighborCount > maxNeighbors) {
                maxNeighbors = neighborCount;
            }
            amountOfNeighbours[i] = neighborCount;
        }

        for (int i = 0; i < unplacedEvents.size(); i++) {
            if (amountOfNeighbours[i] == maxNeighbors) {
                chosenEvents.add(unplacedEvents.get(i));
            }
        }
        return chosenEvents;
    }

    private TimeslotDataNode applyHeuristic3(List<TimeslotDataNode> unplacedEvents) {
        Random random = new Random();
        return unplacedEvents.get(random.nextInt(unplacedEvents.size()));
    }

    private List<Timeslot> applyHeuristic4(List<Timeslot> availableColors,
                                           List<TimeslotDataNode> unplacedEvents, TimeslotDataNode event) {
        List<Timeslot> chosenColors = new ArrayList<>();
        int[] validityCount = new int[availableColors.size()];
        int minValidityCount = Integer.MAX_VALUE;

        for (int i = 0; i < unplacedEvents.size(); i++) {
            if (unplacedEvents.get(i).equals(event)) {
                continue;
            }
            int unplacedEventIndex = graph.getNodes().indexOf(unplacedEvents.get(i));

            for (int j = 0; j < availableColors.size(); j++) {
                int colorIndex = model.getTimeslots().indexOf(availableColors.get(j));
                validityCount[j] += model.getEventAvailabilityMatrix()[unplacedEventIndex][colorIndex];
            }
        }

        for (int i = 0; i < availableColors.size(); i++) {
            if (validityCount[i] < minValidityCount) {
                minValidityCount = validityCount[i];
            }
        }
        for (int i = 0; i < availableColors.size(); i++) {
            if (validityCount[i] == minValidityCount) {
                chosenColors.add(availableColors.get(i));
            }
        }
        return chosenColors;
    }

    private List<Timeslot> applyHeuristic5(List<Timeslot> availableColors) {
        List<Timeslot> chosenColors = new ArrayList<>();
        int[] amountOfEvents = new int[availableColors.size()];
        int minAmountOfEvents = Integer.MAX_VALUE;

        for (Timeslot timeslot : solution.values()) {
            if (availableColors.contains(timeslot)) {
                int timeslotIndex = availableColors.indexOf(timeslot);
                amountOfEvents[timeslotIndex]++;
            }
        }

        for (int i = 0; i < availableColors.size(); i++) {
            if (amountOfEvents[i] < minAmountOfEvents) {
                minAmountOfEvents = amountOfEvents[i];
            }
        }
        for (int i = 0; i < availableColors.size(); i++) {
            if (amountOfEvents[i] == minAmountOfEvents) {
                chosenColors.add(availableColors.get(i));
            }
        }
        return chosenColors;
    }

    private Timeslot applyHeuristic6(List<Timeslot> chosenColors) {
        Random random = new Random();
        return chosenColors.get(random.nextInt(chosenColors.size()));
    }

    private TimeslotDataNode chooseNode(List<TimeslotDataNode> unplacedEvents) {
        List<TimeslotDataNode> chosenEvents = applyHeuristic1(unplacedEvents);
        if (chosenEvents.size() == 1) {
            return chosenEvents.get(0);
        }

        chosenEvents = applyHeuristic2(chosenEvents);
        if (chosenEvents.size() == 1) {
            return chosenEvents.get(0);
        }

        return applyHeuristic3(chosenEvents);
    }

    private Timeslot chooseColor(List<TimeslotDataNode> unplacedEvents, TimeslotDataNode event) {
        List<Timeslot> availableColors = new ArrayList<>(colorList);
        for (TimeslotDataNode neighbor : graph.getNeighbors(event)) {
            if (solution.containsKey(neighbor)) {
                availableColors.remove(solution.get(neighbor));
            }
        }
        List<Timeslot> chosenColor = applyHeuristic4(availableColors, unplacedEvents, event);
        if (chosenColor.size() == 1) {
            return chosenColor.get(0);
        }

        chosenColor = applyHeuristic5(availableColors);
        if (chosenColor.size() == 1) {
            return chosenColor.get(0);
        }

        return applyHeuristic6(chosenColor);
    }

    private void solvemodifiedDSatur() {
        int maxTries = 10000;
        int tries = 0;
        List<TimeslotDataNode> unplacedEvents = new ArrayList<>(graph.getNodes());
        while (!unplacedEvents.isEmpty() && tries < maxTries) {
            TimeslotDataNode event = chooseNode(unplacedEvents);
            Timeslot color = chooseColor(unplacedEvents, event);
            solution.put(event, color);
            unplacedEvents.remove(event);
            tries++;
        }
    }

    public void printSolution() {
        for (Timeslot timeslot : timeslotToNodes.keySet()) {
            System.out.println(timeslot + ":");
            for (TimeslotDataNode node : timeslotToNodes.get(timeslot)) {
                System.out.println("\t" + node);
            }
            System.out.println();
        }

        System.out.println();
        System.out.println("Uncolored nodes:");
        for (TimeslotDataNode node : uncoloredNodes) {
            System.out.println("\t" + node);
        }
    }
}
