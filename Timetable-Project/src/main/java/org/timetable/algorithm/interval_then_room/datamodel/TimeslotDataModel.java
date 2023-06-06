package org.timetable.algorithm.interval_then_room.datamodel;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.timetable.algorithm.interval_then_room.model.RoomFeature;
import org.timetable.algorithm.interval_then_room.model.Timeslot;
import org.timetable.pojo.*;
import org.timetable.util.AlgorithmConstants;

import java.time.LocalTime;
import java.util.*;

@Data
@NoArgsConstructor
public class TimeslotDataModel {
    private static Map<String, String> shortenedTypeNames = Map.of(
            "curs", "C",
            "lab", "L",
            "sem", "S"
    );

    private List<Event> events = new ArrayList<>();
    private List<Timeslot> timeslots = new ArrayList<>();
    private List<Group> studentGroups = new ArrayList<>();
    private List<Prof> profs = new ArrayList<>();
    private List<Resource> rooms = new ArrayList<>();
    private List<RoomFeature> roomFeatures = new ArrayList<>();

    private int[][] studentAttendanceMatrix;
    private int[][] profAttendanceMatrix;
    private int[][] roomFeatureMatrix;
    private int[][] eventFeatureMatrix;
    private int[][] eventAvailabilityMatrix;

    private int[][] roomSuitabilityMatrix;
    private int[][] constraintMatrix;

    public TimeslotDataModel(List<Event> events, List<Group> studentGroups, List<Prof> profs, List<Resource> rooms,
                             List<RoomFeature> roomFeatures) {
        this.events = events;
        this.studentGroups = studentGroups;
        this.profs = profs;
        this.rooms = rooms;
        this.roomFeatures = roomFeatures;


        populateTimeslots();
        populateMatrices();
    }

    public TimeslotDataModel(Timetable timetable) {
        ArrayList<RoomFeature> roomFeatures = new ArrayList<>();
        roomFeatures.add(new RoomFeature("C"));
        roomFeatures.add(new RoomFeature("L"));

        this.events = new ArrayList<>(timetable.getEvents());
        this.studentGroups = new ArrayList<>(timetable.getGroups());
        this.profs = new ArrayList<>(timetable.getProfs());
        this.rooms = new ArrayList<>(timetable.getResources());
        this.roomFeatures = roomFeatures;

        populateTimeslots();
        populateMatrices();
    }

    private void populateTimeslots() {
        // populate timeslots
        for (int day = 0; day < AlgorithmConstants.NUMBER_OF_DAYS; day++) {
            for (LocalTime startTime = AlgorithmConstants.START_TIME; startTime.isBefore(AlgorithmConstants.END_TIME);
                 startTime = startTime.plusHours(AlgorithmConstants.GENERAL_DURATION)) {
                LocalTime endTime = startTime.plusHours(AlgorithmConstants.GENERAL_DURATION);
                timeslots.add(new Timeslot(day, startTime, endTime));
            }
        }
    }

    private void populateMatrices() {
        studentAttendanceMatrix = new int[studentGroups.size()][events.size()];
        profAttendanceMatrix = new int[profs.size()][events.size()];
        roomFeatureMatrix = new int[rooms.size()][roomFeatures.size()];
        eventFeatureMatrix = new int[events.size()][roomFeatures.size()];
        eventAvailabilityMatrix = new int[events.size()][timeslots.size()];

        for (int i = 0; i < studentGroups.size(); i++) {
            for (int j = 0; j < events.size(); j++) {
                if (events.get(j).getGroupList().contains(studentGroups.get(i))) {
                    studentAttendanceMatrix[i][j] = 1;
                }
            }
        }

        for (int i = 0; i < profs.size(); i++) {
            for (int j = 0; j < events.size(); j++) {
                if (events.get(j).getProfList().contains(profs.get(i))) {
                    profAttendanceMatrix[i][j] = 1;
                }
            }
        }

        for (int i = 0; i < rooms.size(); i++) {
            String shortenedType = shortenedTypeNames.get(rooms.get(i).getType());
            if (shortenedType.equals("C")) {
                roomFeatureMatrix[i][0] = 1;
            } else if (shortenedType.equals("L") || shortenedType.equals("S")) {
                roomFeatureMatrix[i][1] = 1;
            }
        }

        for (int i = 0; i < events.size(); i++) {
            String type = events.get(i).getType();
            if (type.equals("C")) {
                eventFeatureMatrix[i][0] = 1;
            } else if (type.equals("L") || type.equals("S")) {
                eventFeatureMatrix[i][1] = 1;
            }
        }

        for (int i = 0; i < events.size(); i++) {
            for (int j = 0; j < timeslots.size(); j++) {
                eventAvailabilityMatrix[i][j] = 1;
            }
        }

        roomSuitabilityMatrix = new int[events.size()][rooms.size()];
        constraintMatrix = new int[events.size()][events.size()];

        for (int i = 0; i < events.size(); i++) {
            for (int j = 0; j < rooms.size(); j++) {
                for (int k = 0; k < roomFeatures.size(); k++) {
                    if (eventFeatureMatrix[i][k] != roomFeatureMatrix[j][k]) {
                        continue;
                    }
                    roomSuitabilityMatrix[i][j] = 1;
                }
            }
        }

        for (int i = 0; i < events.size(); i++) {
            for (int j = 0; j < events.size(); j++) {
                for (int k = 0; k < studentGroups.size(); k++) {
                    if (studentAttendanceMatrix[k][i] == 1 &&
                            studentAttendanceMatrix[k][j] == 1) {
                        constraintMatrix[i][j] = 1;
                        break;
                    }
                }

                for (int k = 0; k < profs.size(); k++) {
                    if (profAttendanceMatrix[k][i] == 1 &&
                            profAttendanceMatrix[k][j] == 1) {
                        constraintMatrix[i][j] = 1;
                        break;
                    }
                }

                int sum1 = 0;
                int sum2 = 0;
                for (int k = 0; k < rooms.size(); k++) {
                    if (roomSuitabilityMatrix[i][k] == 1) {
                        sum1++;
                    }
                    if (roomSuitabilityMatrix[j][k] == 1) {
                        sum2++;
                    }
                }

                boolean isSuitableForBoth = false;
                for (int k = 0; k < rooms.size(); k++) {
                    if (roomSuitabilityMatrix[i][k] == 1 &&
                            roomSuitabilityMatrix[j][k] == 1) {
                        isSuitableForBoth = true;
                        break;
                    }
                }

                if (sum1 == 1 && sum2 == 1 && isSuitableForBoth) {
                    constraintMatrix[i][j] = 1;
                }
            }
        }
    }
}
