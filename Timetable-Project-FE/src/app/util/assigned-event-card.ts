import { AssignedTimetableEvent } from "../model/assigned-timetable-event";

export interface AssignedEventCard {
    events: AssignedTimetableEvent[];
    currentIndex: number;
}