package org.timetable.algorithm.interval_then_room.datamodel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.timetable.algorithm.interval_then_room.datamodel.TimeslotDataModel;
import org.timetable.algorithm.interval_then_room.model.RoomDataNode;
import org.timetable.algorithm.interval_then_room.model.Timeslot;
import org.timetable.algorithm.interval_then_room.model.TimeslotDataNode;
import org.timetable.pojo.Event;
import org.timetable.pojo.Resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SoftConstraintsModel {
    private TimeslotDataModel hardConstraintsModel;
    private Map<Timeslot, Map<Event, Resource>> solution;
    private int[][] studentTimeslotAttendanceMatrix;
    private int[][] studentOneDayAttendanceMatrix;

    public SoftConstraintsModel(TimeslotDataModel model, Map<Timeslot,
                                Map<TimeslotDataNode, RoomDataNode>> timeslotToSolution) {
        this.hardConstraintsModel = model;
        Map<Timeslot, Map<Event, Resource>> solutionTemp = new HashMap<>();
        for (Timeslot timeslot : timeslotToSolution.keySet()) {
            Map<Event, Resource> eventResourceMap = new HashMap<>();
            for (TimeslotDataNode eventNode : timeslotToSolution.get(timeslot).keySet()) {
                if (eventNode == null) {
                    continue;
                }
                RoomDataNode roomNode = timeslotToSolution.get(timeslot).get(eventNode);
                if (roomNode == null) {
                    continue;
                }
                eventResourceMap.put(eventNode.getEvent(), roomNode.getResource());
            }
            solutionTemp.put(timeslot, eventResourceMap);
        }
        this.solution = solutionTemp;

        studentTimeslotAttendanceMatrix = new int[model.getStudentGroups().size()][model.getTimeslots().size()];
        studentOneDayAttendanceMatrix = new int[model.getStudentGroups().size()][TimeslotDataModel.DAYS_IN_WEEK - 1];

        for (int i = 0; i < model.getStudentGroups().size(); i++) {
            for (int j = 0; j < model.getTimeslots().size(); j++) {
                Timeslot timeslot = model.getTimeslots().get(j);
                if (!solution.containsKey(timeslot)) {
                    continue;
                }
                List<Event> eventsForTimeslot = solution.get(timeslot).keySet().stream().toList();

                for (Event event : eventsForTimeslot) {
                    int eventIndex = model.getEvents().indexOf(event);
                    if (model.getStudentAttendanceMatrix()[i][eventIndex] == 1) {
                        studentTimeslotAttendanceMatrix[i][j] = 1;
                        break;
                    }
                }
            }
        }

        for (int i = 0; i < model.getStudentGroups().size(); i++) {
            for (int j = 0; j < TimeslotDataModel.DAYS_IN_WEEK - 1; j++) {
                studentOneDayAttendanceMatrix[i][j] = 0;
            }

            for (int j = 0; j < model.getTimeslots().size(); j++) {
                Timeslot timeslot = model.getTimeslots().get(j);
                if (timeslot.getDay() == TimeslotDataModel.DAYS_IN_WEEK - 1) {
                    continue;
                }
                if (!solution.containsKey(timeslot)) {
                    continue;
                }

                List<Event> eventsForTimeslot = solution.get(timeslot).keySet().stream().toList();
                for (Event event : eventsForTimeslot) {
                    int eventIndex = model.getEvents().indexOf(event);
                    if (model.getStudentAttendanceMatrix()[i][eventIndex] == 1) {
                        studentOneDayAttendanceMatrix[i][timeslot.getDay()]++;
                    }
                }
            }
        }
    }

    public int calculateSoftConstraintsCost() {
        int cost = 0;
        for (int i = 0; i < hardConstraintsModel.getStudentGroups().size(); i++) {
            for (int j = 1; j <= 5; j++) {
                // cost for attending 1 event in the last timeslot of the day
                int insideCost = studentTimeslotAttendanceMatrix[i][6 * j - 1];

                // cost for attending 3 consecutive events
                for (int k = 1; k <= 7; k++) {
                    int prod = 1;
                    for (int l = 0; l <= 2; l++) {
                        prod *= studentTimeslotAttendanceMatrix[i][6 * (j - 1) + k + l - 1];
                    }
                    insideCost += prod;
                }

                // cost for attending 1 event in a day
                insideCost += studentOneDayAttendanceMatrix[i][j - 1];
                cost += insideCost;
            }
        }
        return cost;
    }
}
