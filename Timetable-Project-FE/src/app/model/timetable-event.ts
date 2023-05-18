import { StudentGroup } from "./student-group";
import { Professor } from "./professor";

export interface TimetableEvent {
    abbr: string;
    actors: string;
    duration: number;
    frequency: number;
    eventGroup: string;
    name: string;
    notes: string;
    type: string;

    studentGroupAbbrsList: string[];
    profAbbrsList: string[];
}