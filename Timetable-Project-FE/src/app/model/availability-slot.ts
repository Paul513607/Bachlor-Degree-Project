import { Resource } from "./resource";

export interface AvailabilitySlot {
    available: boolean,
    rooms: Resource[],
    day: number,
    time: string;
}