package org.timetable.algorithm.interval_then_room;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.timetable.algorithm.interval_then_room.datamodel.TimeslotDataModel;
import org.timetable.algorithm.interval_then_room.model.*;
import org.timetable.util.Pair;

import java.util.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PartialColAlgorithm {
    private TimeslotDataModel hardConstraintsModel;
    private Map<Timeslot, Set<TimeslotDataNode>> initialSolution;
    private Set<TimeslotDataNode> initialUnplacedEvents;
    private TimeslotDataGraph timeslotDataGraph;
    private int maxIterations;

    private int[][] tabuMatrix;

    private Map<Timeslot, Set<TimeslotDataNode>> bestSolution;
    private Set<TimeslotDataNode> bestUnplacedEvents;

    private float tabuValue = 0.6f;
    private float tabuMoveProbability = 0.5f;


    public PartialColAlgorithm(TimeslotDataModel model,
                               Map<Timeslot, Set<TimeslotDataNode>> timeslotToEvent,
                               Set<TimeslotDataNode> unplacedEvents,
                               TimeslotDataGraph timeslotDataGraph,
                               int maxIterations) {
        this.hardConstraintsModel = model;
        this.initialSolution = timeslotToEvent;
        this.initialUnplacedEvents = unplacedEvents;
        this.timeslotDataGraph = timeslotDataGraph;
        this.maxIterations = maxIterations;

        this.tabuMatrix = new int[model.getEvents().size()][model.getTimeslots().size()];
    }

    public void solve() {
        bestSolution = new HashMap<>(initialSolution);
        bestUnplacedEvents = new HashSet<>(initialUnplacedEvents);
        int bestCost = getCost();
        for (int i = 0; i < maxIterations; i++) {
            // explore neighborhoods
            for (Timeslot timeslot : bestSolution.keySet()) {
                Set<TimeslotDataNode> events = bestSolution.get(timeslot);
                for (TimeslotDataNode event : events) {
                    var pair = exploreNeighbor(event, timeslot, bestSolution, bestUnplacedEvents);
                    if (pair == null) {
                        continue;
                    }

                    // check if tabu move and skip with a probability
                    int timeslotIndex = hardConstraintsModel.getTimeslots().indexOf(timeslot);
                    int eventIndex = hardConstraintsModel.getEvents().indexOf(event.getEvent());
                    if (tabuMatrix[eventIndex][timeslotIndex] > 0) {
                        Random random = new Random();
                        float randomValue = random.nextFloat();
                        if (randomValue > tabuMoveProbability) {
                            continue;
                        }
                    }

                    var currentSolution = pair.getFirst();
                    var currentUnplacedEvents = pair.getSecond();
                    int currentCost = getCost();
                    if (currentCost < bestCost) {
                        bestSolution = currentSolution;
                        bestUnplacedEvents = currentUnplacedEvents;
                        bestCost = currentCost;
                    }

                    if (bestCost == 0) {
                        return;
                    }
                }
            }

            // update tabu matrix
            for (int j = 0; j < tabuMatrix.length; j++) {
                for (int k = 0; k < tabuMatrix[j].length; k++) {
                    if (tabuMatrix[j][k] > 0) {
                        tabuMatrix[j][k]--;
                    }
                }
            }

            System.out.println("Iteration " + i + " best cost: " + bestCost);
        }
    }

    private int getCost() {
        return bestUnplacedEvents.size();
    }

    private Pair<Map<Timeslot, Set<TimeslotDataNode>>, Set<TimeslotDataNode>> exploreNeighbor(TimeslotDataNode event, Timeslot timeslot,
                                                                                              Map<Timeslot, Set<TimeslotDataNode>> currentSolution,
                                                                                              Set<TimeslotDataNode> currentUnplacedEvents) {
        Set<TimeslotDataNode> currentEvents = currentSolution.get(timeslot);
        Set<TimeslotDataNode> otherUnplacedEvents = new HashSet<>();
        currentEvents.add(event);
        List<TimeslotDataNode> neighborEvents = timeslotDataGraph.getNeighbors(event);
        for (TimeslotDataNode currentEvent : currentEvents) {
            if (neighborEvents.contains(currentEvent)) {
                otherUnplacedEvents.add(currentEvent);
                continue;
            }
            currentEvents.add(currentEvent);
        }

        RoomDataGraph graph = new RoomDataGraph(hardConstraintsModel, currentEvents);
        RoomBipartiteSolver bipartiteSolver = new RoomBipartiteSolver(graph);
        bipartiteSolver.hopcroftKarpSolver();
        Map<TimeslotDataNode, RoomDataNode> eventToRoom = bipartiteSolver.getEventPairs();
        boolean isSwapOk = true;
        for (TimeslotDataNode currentEvent : eventToRoom.keySet()) {
            if (currentEvent == null) {
                continue;
            }

            if (eventToRoom.get(currentEvent) == null) {
                isSwapOk = false;
                break;
            }
        }

        if (isSwapOk) {
            currentSolution.put(timeslot, currentEvents);
            currentUnplacedEvents.addAll(otherUnplacedEvents);
            addMoveToTabuList(event, timeslot);
            return Pair.of(currentSolution, currentUnplacedEvents);
        }
        return null;
    }

    public void addMoveToTabuList(TimeslotDataNode event, Timeslot timeslot) {
        int indexOfTimeslot = hardConstraintsModel.getTimeslots().indexOf(timeslot);
        int indexOfEvent = hardConstraintsModel.getEvents().indexOf(event.getEvent());

        Random random = new Random();
        int val = random.nextInt(10) + 1;
        tabuMatrix[indexOfEvent][indexOfTimeslot] = (int) (tabuValue * getCost()) + val;
    }
}
