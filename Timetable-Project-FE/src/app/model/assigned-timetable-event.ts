import { TimetableEvent } from "./timetable-event";
import { Resource } from "./resource";

export interface AssignedTimetableEvent {
    timetableEvent: TimetableEvent;
    resource: Resource;
    day: number; // from 0 to 6
    time: string;
}