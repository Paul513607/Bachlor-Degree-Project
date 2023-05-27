import { Resource } from "./resource";

export interface AvailabilitySlot {
    rooms: Resource[],
    day: number,
    hour: number,
}