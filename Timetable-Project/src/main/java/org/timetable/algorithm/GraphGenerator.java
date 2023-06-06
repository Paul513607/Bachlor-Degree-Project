package org.timetable.algorithm;

import org.timetable.generic_model.*;
import org.timetable.pojo.Timetable;

import java.util.List;

public interface GraphGenerator {
    TimetableGraph<TimetableNode, TimetableEdge> createGraph(Timetable timetable);
    List<? extends TimetableColor> createTimetableLaboratoryColors(Timetable timetable);
    List<? extends TimetableColor> createTimetableCourseColors(Timetable timetable);
}
