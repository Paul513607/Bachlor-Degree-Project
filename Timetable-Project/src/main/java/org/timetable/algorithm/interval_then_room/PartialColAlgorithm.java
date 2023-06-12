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

    private Map<Timeslot, Set<TimeslotDataNode>> bestIterationSolution;
    private Set<TimeslotDataNode> bestIterationUnplacedEvents;

    private float tabuValue = 0.6f;
    private float tabuMoveProbability = 0.5f;
    private int initialCost;


    public PartialColAlgorithm(TimeslotDataModel model,
                               Map<Timeslot, Set<TimeslotDataNode>> timeslotToEvent,
                               Set<TimeslotDataNode> unplacedEvents,
                               TimeslotDataGraph timeslotDataGraph,
                               int maxIterations) {
        this.hardConstraintsModel = model;
        this.initialSolution = timeslotToEvent;
        this.initialUnplacedEvents = unplacedEvents;
        this.initialCost = this.initialUnplacedEvents.size();
        this.timeslotDataGraph = timeslotDataGraph;
        this.maxIterations = maxIterations;

        this.tabuMatrix = new int[model.getEvents().size()][model.getTimeslots().size()];
    }

    private Timeslot getRandomTimeslot(Map<Timeslot, Set<TimeslotDataNode>> solution) {
        Random random = new Random();
        int randomIndex = random.nextInt(solution.size());
        List<Timeslot> timeslots = new ArrayList<>(solution.keySet());
        return timeslots.get(randomIndex);
    }

    private TimeslotDataNode getRandomEvent(Set<TimeslotDataNode> unplacedEvents) {
        Random random = new Random();
        int randomIndex = random.nextInt(unplacedEvents.size());
        List<TimeslotDataNode> events = new ArrayList<>(unplacedEvents);
        return events.get(randomIndex);
    }

    public void solve() {
        Random random = new Random();

        bestSolution = new HashMap<>(initialSolution);
        bestUnplacedEvents = new HashSet<>(initialUnplacedEvents);
        bestIterationSolution = new HashMap<>(initialSolution);
        bestIterationUnplacedEvents = new HashSet<>(initialUnplacedEvents);
        for (int i = 0; i < maxIterations; i++) {

            var currentSolution = new HashMap<>(bestIterationSolution);
            var currentUnplacedEvents = new HashSet<>(bestIterationUnplacedEvents);

            Map<Pair<Integer, Integer>, Integer> moveDeltas = new HashMap<>();
            for (TimeslotDataNode event : currentUnplacedEvents) {
                for (Timeslot timeslot : currentSolution.keySet()) {
                    int eventIndex = hardConstraintsModel.getEvents().indexOf(event.getEvent());
                    int timeslotIndex = hardConstraintsModel.getTimeslots().indexOf(timeslot);

                    if (tabuMatrix[eventIndex][timeslotIndex] > 0) {
                        continue;
                    }

                    var pair = exploreNeighbor(event, timeslot, currentSolution, currentUnplacedEvents);
                    if (pair == null) {
                        continue;
                    }
                    var neighborSolution = new HashMap<>(pair.getFirst());
                    var neighborUnplacedEvents = new HashSet<>(pair.getSecond());

                    int neighborCost = neighborUnplacedEvents.size();
                    int deltaCost = neighborCost - getCost(bestIterationUnplacedEvents);
                    moveDeltas.put(new Pair<>(eventIndex, timeslotIndex), deltaCost);
                    addMoveToTabuList(event, timeslot);
                }
            }

            if (moveDeltas.isEmpty()) {
                // move a random event from a random timeslot
                Timeslot timeslot = getRandomTimeslot(currentSolution);
                if (!currentSolution.get(timeslot).isEmpty()) {
                    TimeslotDataNode event = getRandomEvent(currentSolution.get(timeslot));

                    currentSolution.get(timeslot).remove(event);
                    currentUnplacedEvents.add(event);
                }
            } else {
                // move the event with the best delta
                Pair<Integer, Integer> bestMove = null;
                int bestDelta = Integer.MAX_VALUE;
                for (Pair<Integer, Integer> move : moveDeltas.keySet()) {
                    int delta = moveDeltas.get(move);
                    if (delta < bestDelta) {
                        bestDelta = delta;
                        bestMove = move;
                    }
                }

                int eventIndex = bestMove.getFirst();
                int timeslotIndex = bestMove.getSecond();
                TimeslotDataNode event = new TimeslotDataNode(hardConstraintsModel.getEvents().get(eventIndex));
                Timeslot timeslot = hardConstraintsModel.getTimeslots().get(timeslotIndex);

                var pair = performMove(event, timeslot, currentSolution, currentUnplacedEvents);

                currentSolution = new HashMap<>(pair.getFirst());
                currentUnplacedEvents = new HashSet<>(pair.getSecond());
            }

            // accept move
            bestIterationSolution = new HashMap<>(currentSolution);
            bestIterationUnplacedEvents = new HashSet<>(currentUnplacedEvents);

            // update tabu matrix
            for (int j = 0; j < tabuMatrix.length; j++) {
                for (int k = 0; k < tabuMatrix[j].length; k++) {
                    if (tabuMatrix[j][k] > 0) {
                        tabuMatrix[j][k]--;
                    }
                }
            }

            // update best solution
            if (getCost(bestIterationUnplacedEvents) < getCost(bestUnplacedEvents)) {
                bestSolution = new HashMap<>(bestIterationSolution);
                bestUnplacedEvents = new HashSet<>(bestIterationUnplacedEvents);
            }
        }
    }

    private int getCost(Set<TimeslotDataNode> unplacedEvents) {
        return unplacedEvents.size();
    }

    private Pair<Map<Timeslot, Set<TimeslotDataNode>>, Set<TimeslotDataNode>> exploreNeighbor(
                                                                TimeslotDataNode event, Timeslot timeslot,
                                                                Map<Timeslot, Set<TimeslotDataNode>> currentSolution,
                                                                Set<TimeslotDataNode> currentUnplacedEvents) {
        var neighborSolution = new HashMap<>(currentSolution);
        var neighborUnplacedEvents = new HashSet<>(currentUnplacedEvents);

        Set<TimeslotDataNode> currentEvents = new HashSet<>(neighborSolution.get(timeslot));
        Set<TimeslotDataNode> otherUnplacedEvents = new HashSet<>();
        neighborUnplacedEvents.remove(event);
        List<TimeslotDataNode> neighborEvents = timeslotDataGraph.getNeighbors(event);
        for (TimeslotDataNode neighborEvent : neighborEvents) {
            if (currentEvents.contains(neighborEvent)) {
                otherUnplacedEvents.add(neighborEvent);
            }
        }
        currentEvents.removeAll(otherUnplacedEvents);
        currentEvents.add(event);

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
            neighborSolution.put(timeslot, currentEvents);
            neighborUnplacedEvents.addAll(otherUnplacedEvents);
            return Pair.of(neighborSolution, neighborUnplacedEvents);
        }
        return null;
    }

    private Pair<Map<Timeslot, Set<TimeslotDataNode>>, Set<TimeslotDataNode>> performMove(
            TimeslotDataNode event, Timeslot timeslot,
            Map<Timeslot, Set<TimeslotDataNode>> currentSolution,
            Set<TimeslotDataNode> currentUnplacedEvents) {
        var neighborSolution = new HashMap<>(currentSolution);
        var neighborUnplacedEvents = new HashSet<>(currentUnplacedEvents);

        Set<TimeslotDataNode> currentEvents = new HashSet<>(neighborSolution.get(timeslot));
        Set<TimeslotDataNode> otherUnplacedEvents = new HashSet<>();
        neighborUnplacedEvents.remove(event);
        List<TimeslotDataNode> neighborEvents = timeslotDataGraph.getNeighbors(event);
        for (TimeslotDataNode neighborEvent : neighborEvents) {
            if (currentEvents.contains(neighborEvent)) {
                otherUnplacedEvents.add(neighborEvent);
            }
        }
        currentEvents.removeAll(otherUnplacedEvents);
        currentEvents.add(event);

        neighborUnplacedEvents.addAll(otherUnplacedEvents);
        neighborSolution.put(timeslot, currentEvents);
        return Pair.of(neighborSolution, neighborUnplacedEvents);
    }

    public void addMoveToTabuList(TimeslotDataNode event, Timeslot timeslot) {
        int indexOfTimeslot = hardConstraintsModel.getTimeslots().indexOf(timeslot);
        int indexOfEvent = hardConstraintsModel.getEvents().indexOf(event.getEvent());

        Random random = new Random();
        int val = random.nextInt(10) + 1;
        tabuMatrix[indexOfEvent][indexOfTimeslot] = (int) (tabuValue * getCost(bestIterationUnplacedEvents)) + val;
    }
}
