import { TimetableEvent } from "./timetable-event";
import { Resource } from "./resource";

export interface AssignedTimetableEvent {
    event: TimetableEvent | null;
    resource: Resource | null;
    day: number | null; // from 0 to 6
    time: string | null;
}

export let emptyAssignedTimetableEvent: AssignedTimetableEvent = {
    event: null,
    resource: null,
    day: null,
    time: null
}