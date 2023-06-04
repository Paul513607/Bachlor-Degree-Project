package org.timetable.algorithm.partialcol;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.timetable.algorithm.partialcol.model.Timeslot;
import org.timetable.algorithm.partialcol.model.TimeslotDataGraph;
import org.timetable.algorithm.partialcol.model.TimeslotDataNode;

import java.util.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimeslotColoringSolver {
    private TimeslotDataGraph graph;
    private List<Timeslot> colorList = new ArrayList<>();
    private Map<TimeslotDataNode, Timeslot> solution = new HashMap<>();
    private Map<Timeslot, Set<TimeslotDataNode>> timeslotToNodes = new HashMap<>();
    private List<TimeslotDataNode> uncoloredNodes = new ArrayList<>();

    private int algorithmOption = 1;

    public TimeslotColoringSolver(TimeslotDataModel model) {
        graph = new TimeslotDataGraph(model);
        colorList = new ArrayList<>(model.getTimeslots());
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
            solveDSatur();
        }

        formatSolution();
    }

    private void solveGreedy() {
        for (TimeslotDataNode node : graph.getNodes()) {
            List<Timeslot> availableColors = new ArrayList<>(colorList);
            for (TimeslotDataNode neighbor : graph.getNeighbours(node)) {
                if (solution.containsKey(neighbor)) {
                    availableColors.remove(solution.get(neighbor));
                }
            }
            if (!availableColors.isEmpty()) {
                solution.put(node, availableColors.get(0));
            }
        }
    }

    private void solveDSatur() {

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
