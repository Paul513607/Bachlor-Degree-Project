import { Component, EventEmitter, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { ActivatedRoute, Router } from '@angular/router';
import { BehaviorSubject, Observable, Subject, lastValueFrom, takeUntil } from 'rxjs';
import { AssignedTimetableEvent, emptyAssignedTimetableEvent } from 'src/app/model/assigned-timetable-event';
import { TimetableEvent } from 'src/app/model/timetable-event';
import { TimetableService } from 'src/app/services/timetable.service';
import { RowSpanCalculator, Span } from '../../../util/rowspan-calculator';
import { AvailabilitySlotService } from 'src/app/services/availability-slot.service';
import { AvailabilitySlot } from 'src/app/model/availability-slot';
import { Resource } from 'src/app/model/resource';
import { EventService } from 'src/app/services/event.service';
import { ResourceService } from 'src/app/services/resource.service';

@Component({
  selector: 'app-timetable',
  templateUrl: './timetable.component.html',
  styleUrls: ['./timetable.component.css']
})
export class TimetableComponent implements OnInit, OnDestroy {
  START_TIME: number = 7;
  END_TIME: number = 20;
  GENERAL_CLASS_DURATION: number = 2;

  public days: string[] = ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'];
  private daysToValues: Map<string, number> = new Map<string, number>([
    ['Mon', 0],
    ['Tue', 1],
    ['Wed', 2],
    ['Thu', 3],
    ['Fri', 4],
    ['Sat', 5],
    ['Sun', 6]
  ]);
  public classTimes: string[] = [];

  public assignedEvents$: Observable<AssignedTimetableEvent[]> = new Observable<AssignedTimetableEvent[]>();
  public assignedEvents: AssignedTimetableEvent[] = [];

  public roomList: Resource[] = [];

  public columnsToDisplay: string[] = ['rowClassTime', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'];
  public columnsWithoutFirst: string[] = [];
  public timetableData: any[] = [];
  public rowSpans: Array<Span[]> = [];

  public unsubscribe$: Subject<void> = new Subject<void>();

  public selectedAlgorithmOption: string = '';
  public useSorting: boolean = false;
  public shuffleEvents: boolean = false;

  public selectedStudentGroup: string = '';
  public selectedProfessor: string = '';
  public selectedRoom: string = '';

  public currentAssignedEvent: AssignedTimetableEvent = emptyAssignedTimetableEvent;
  public currentEventAvailabilitySlots: AvailabilitySlot[] = [];

  public DEFAULT_CELL_COLOR: string = 'rgba(255, 255, 255)';
  public UNAVAILABLE_CELL_COLOR: string = 'rgba(255, 0, 0, 0.5)';
  public AVAILABLE_CELL_COLOR: string = 'rgba(0, 255, 0, 0.5)';
  public cellToColorMatrix = new Array<Array<string>>();

  public checkingAvailability: boolean = false;
  public currentEventRooms: Resource[] = [];
  public isAssignmentOverlap: boolean = false;

  public unassignedEvents: TimetableEvent[] = [];

  public isLoading: boolean = false;

  constructor(
    private timetableService: TimetableService,
    private eventService: EventService,
    private resourceService: ResourceService,
    private availabilitySlotService: AvailabilitySlotService,
    private router: Router,
    private readonly activatedRoute: ActivatedRoute
  ) {
    this.columnsWithoutFirst = this.columnsToDisplay.slice(1);

    for (let hour: number = this.START_TIME; hour < this.END_TIME; hour += 1) {
      let classTime: string = hour.toString();
      const currentRow: any = { rowClassTime: classTime };
      let dayIdx: number = 0;
      this.cellToColorMatrix.push(new Array<string>());
      for (const day of this.days) {
        currentRow[day] = {
          events: [emptyAssignedTimetableEvent],
          currentIndex: 0
        };
        this.cellToColorMatrix[hour - this.START_TIME][dayIdx] = this.DEFAULT_CELL_COLOR;
        dayIdx++;
      }
      this.timetableData.push(currentRow);
    }

    const rowSpanCalculator: RowSpanCalculator = new RowSpanCalculator();
    this.rowSpans = rowSpanCalculator.calculateSpans(this.timetableData, this.columnsToDisplay);

    console.log(this.cellToColorMatrix);
    this.populateRooms();
  }

  ngOnInit(): void {
  }

  ngOnDestroy(): void {
    this.unsubscribe$.next();
    this.unsubscribe$.complete();
  }

  public populateRooms(): void {
    this.resourceService.getAllRooms()
    .pipe(takeUntil(this.unsubscribe$))
    .subscribe((rooms: any[]) => {
      rooms.forEach(room => {
        this.roomList.push(room);
      });
    });
  }

  public onChageSelectedAlgorithm(algorithmData: any): void {
    this.selectedAlgorithmOption = algorithmData.algorithm;
    this.useSorting = algorithmData.useSorting;
    this.shuffleEvents = algorithmData.shuffleEvents;
  }

  public onChangeStudentGroup(studentGroup: string): void {
    this.selectedStudentGroup = studentGroup;

    if (this.selectedStudentGroup !== '') {
      this.getAssignedEventsByStudentGroup();
    }
  }

  public onChangeProfessor(professor: string): void {
    this.selectedProfessor = professor;

    if (this.selectedProfessor !== '') {
      this.getAssignedEventsByProfessor();
    }
  }

  public onChangeRoom(room: string): void {
    this.selectedRoom = room;

    if (this.selectedRoom !== '') {
      this.getAssignedEventsByRoom();
    }
  }

  public onGenerateTimetable(): void {
    console.log("Generating timetable...");
    if (this.selectedAlgorithmOption !== '') {
      this.getAssignedEventsByAlgorithmOption();
    }
    console.log("Done generating");
  }

  private updateUnassignedEvents(): void {
    this.eventService.getAllUnassignedEvents()
    .subscribe((unassignedEvents: TimetableEvent[]) => {
      this.unassignedEvents = unassignedEvents;
    });
  }

  public getAssignedEventsByAlgorithmOption(): void {
    if (this.selectedAlgorithmOption === '') {
      return;
    }
    this.isLoading = true;

    console.log(this.selectedAlgorithmOption);
    console.log(this.useSorting);
    console.log(this.shuffleEvents);
    this.timetableService.getAllAssignedEventsWithAlgorithm(this.selectedAlgorithmOption, this.useSorting, this.shuffleEvents)
    .subscribe((assignedEvents: AssignedTimetableEvent[]) => {
      this.assignedEvents = assignedEvents;
      this.updateUnassignedEvents();
      console.log(this.assignedEvents);
      this.isLoading = false;
    });
  }

  private getAssignedEventsByStudentGroup(): void {
    this.isLoading = true;
    this.assignedEvents$ = this.timetableService.getAllAssignedEventsWithStudentGroup(this.selectedStudentGroup);
    this.placeAssignedEventsOnTimetable(this.assignedEvents);
  }

  private getAssignedEventsByProfessor(): void {
    this.isLoading = true;
    this.assignedEvents$ = this.timetableService.getAllAssignedEventsWithProfessor(this.selectedProfessor);
    this.placeAssignedEventsOnTimetable(this.assignedEvents);
  }

  private getAssignedEventsByRoom(): void {
    this.isLoading = true;
    this.assignedEvents$ = this.timetableService.getAllAssignedEventsWithRoom(this.selectedRoom);
    this.placeAssignedEventsOnTimetable(this.assignedEvents);
  }

  private placeAssignedEventsOnTimetable(assignedEvents: AssignedTimetableEvent[]): void {
    this.assignedEvents$
    .subscribe((assignedEvents: AssignedTimetableEvent[]) => {
      this.assignedEvents = assignedEvents;
      this.addEventsToTimetableData(this.assignedEvents);
      this.isLoading = false;
    });
  }

  private addEventsToTimetableData(assignedEvents: AssignedTimetableEvent[]): void {
    console.log(assignedEvents);
    this.timetableData = [];
    this.cellToColorMatrix = [];

    for (let hour = this.START_TIME; hour < this.END_TIME; hour += 1) {
      const classTime: string = hour.toString();
      const currentRow: any = { rowClassTime: classTime };
      this.cellToColorMatrix.push(new Array<string>());
      let dayIdx: number = 0;
      for (const day of this.days) {
        let currentAssignedEvents: AssignedTimetableEvent[] = this.getAssignedEventByDayAndTime(day, hour, assignedEvents);
        currentRow[day] = {
          events: currentAssignedEvents,
          currentIndex: 0
        };
        this.cellToColorMatrix[hour - this.START_TIME][dayIdx] = this.DEFAULT_CELL_COLOR;
        dayIdx++;
      }
      this.timetableData.push(currentRow);
    }

    // update spans
    this.rowSpans = [];
    const rowSpanCalculator = new RowSpanCalculator();
    this.rowSpans = rowSpanCalculator.calculateSpans(this.timetableData, this.columnsToDisplay);
    console.log(this.rowSpans);
    console.log(this.timetableData);
  }

  public goToPreviousElement(timetableCard: any): void {
    timetableCard.currentIndex -= 1;
  }

  public goToNextElement(timetableCard: any): void {
    timetableCard.currentIndex += 1;
  }

  public getRowSpan(element: any, rowIdx: number, colIdx: number): number {
    return this.rowSpans[rowIdx][colIdx].span;
  }

  public isHalfHourEvent(assignedEvent: AssignedTimetableEvent): boolean {
    if (assignedEvent.time === null) {
      return false;
    }

    const assignedEventMinute: number = parseInt(assignedEvent.time.split(":")[0]);
    if (assignedEventMinute === 30) {
      return true;
    }
    return false;
  }

  private getAssignedEventByDayAndTime(day: string, hour: number, 
                    assignedEvents: AssignedTimetableEvent[]): AssignedTimetableEvent[] {
    const assignedEventsForTimeslot: AssignedTimetableEvent[] = [];
    for (let assignedEvent of assignedEvents) {
      if (assignedEvent.time == null) {
        continue;
      }

      const assignedEventHour: number = parseInt(assignedEvent.time.split(":")[0]);
      if (assignedEvent.day === this.daysToValues.get(day) && assignedEventHour === hour) {
        assignedEventsForTimeslot.push(assignedEvent);
      }
    }
    if (assignedEventsForTimeslot.length === 0) {
      assignedEventsForTimeslot.push(emptyAssignedTimetableEvent);
    }
    return assignedEventsForTimeslot;
  }

  public updateCurrentAssignedEvent(assignedEvent: AssignedTimetableEvent): void {
    this.currentAssignedEvent = assignedEvent;
    this.toggleAvailabilityFalse();
  }

  public displayUnassignedEvent(currentEvent: AssignedTimetableEvent): void {
    this.currentAssignedEvent = currentEvent;
    this.toggleAvailabilityFalse();
  }

  public closeEventCard(): void {
    this.currentEventAvailabilitySlots = [];
    this.toggleAvailabilityFalse();
  }

  public toggleAvailabilityTrue(event: TimetableEvent): void {
    this.checkingAvailability = true;
    this.currentEventAvailabilitySlots = [];

    this.availabilitySlotService.getAvailabilitySlotsByEvent(event)
    .subscribe((availabilitySlots: AvailabilitySlot[]) => {
      console.log(availabilitySlots);
      this.currentEventAvailabilitySlots = availabilitySlots;

      for (let hour = this.START_TIME; hour < this.END_TIME; hour += 1) {
        let dayIdx: number = 0;
        for (const day of this.days) {
          let currentAvailabilitySlot: AvailabilitySlot | null = this.getAvailabilitySlotByDayAndTime(day, hour);
          if (currentAvailabilitySlot == null) {
            continue;
          }

          if (this.cellToColorMatrix[hour - this.START_TIME][dayIdx] === this.DEFAULT_CELL_COLOR) {
            console.log(currentAvailabilitySlot);
            if (!currentAvailabilitySlot.available) {
              this.cellToColorMatrix[hour - this.START_TIME][dayIdx] = this.UNAVAILABLE_CELL_COLOR;

              let nextRow: number = hour + 1;
              for (let i = 1; i < event.duration; i++) {
                this.cellToColorMatrix[nextRow - this.START_TIME][dayIdx] = this.UNAVAILABLE_CELL_COLOR;
                nextRow++;
              }

            } else {
              this.cellToColorMatrix[hour - this.START_TIME][dayIdx] = this.AVAILABLE_CELL_COLOR;
            }
          }
          dayIdx++;
        }
      }
    });
  }

  public toggleAvailabilityFalse(): void {
    this.checkingAvailability = false;
    this.isAssignmentOverlap = false;
    this.currentEventAvailabilitySlots = [];

    for (let hour = this.START_TIME; hour < this.END_TIME; hour += 1) {
      let dayIdx: number = 0;
      for (const day of this.days) {
        this.cellToColorMatrix[hour - this.START_TIME][dayIdx] = this.DEFAULT_CELL_COLOR;
        dayIdx++;
      }
    }

    this.currentEventRooms = [];
  }

  public getCellColor(event: AssignedTimetableEvent[], rowIdx: number, colIdx: number): string {
    let color: string = this.DEFAULT_CELL_COLOR;
    const colorOpt: string | undefined = this.cellToColorMatrix[rowIdx][colIdx];
    if (colorOpt != null) {
      color = colorOpt;
    }
    return color;
  }

  private getAvailabilitySlotByDayAndTime(day: string, hour: number): AvailabilitySlot | null {
    for (let availabilitySlot of this.currentEventAvailabilitySlots) {
      const availabilitySlotHour: number = parseInt(availabilitySlot.time.split(":")[0]);
      if (availabilitySlot.day === this.daysToValues.get(day) && availabilitySlotHour === hour) {
        return availabilitySlot;
      }
    }
    return null;
  }

  public getRoomsForDayAndTime(timeslot: any): void {
    let day: string = timeslot.day;
    let hour: number = timeslot.hour;
    let minute: number = timeslot.minute;

    const currentAvailabilitySlot: AvailabilitySlot | null = this.getAvailabilitySlotByDayAndTime(day, hour);
    if (currentAvailabilitySlot == null) {
      this.currentEventRooms = [];
      return;
    }

    this.currentEventRooms = currentAvailabilitySlot.rooms;
    this.isAssignmentOverlap = !currentAvailabilitySlot.available;
  }

  public assignEvent(newAssignedEvent: AssignedTimetableEvent): void {
    console.log(newAssignedEvent);
    if (this.currentAssignedEvent.resource == null) {
      this.timetableService.createAssignedEvent(newAssignedEvent)
      .subscribe(() => {
        this.updateTimetable();
        this.closeEventCard();
        this.currentAssignedEvent = emptyAssignedTimetableEvent;
        this.updateUnassignedEvents();
      });
    } else {
      this.timetableService.updateAssignedEvent(newAssignedEvent)
      .subscribe(() => {
        this.updateTimetable();
        this.closeEventCard();
        this.currentAssignedEvent = emptyAssignedTimetableEvent;
      });
    }
  }

  public updateTimetable(): void {
    if (this.selectedStudentGroup !== '') {
      this.getAssignedEventsByStudentGroup();
    } else if (this.selectedProfessor !== '') {
      this.getAssignedEventsByProfessor();
    } else if (this.selectedRoom !== '') {
      this.getAssignedEventsByRoom();
    } else {
      this.getAssignedEventsByAlgorithmOption();
    }
  }

  public deleteEvent(eventToDelete: AssignedTimetableEvent): void {
    this.timetableService.getIdForAssignedEvent(eventToDelete)
    .subscribe((id: number) => {
      this.timetableService.deleteAssignedEvent(id)
      .subscribe(() => {
        this.updateTimetable();
        this.closeEventCard();
        this.currentAssignedEvent = emptyAssignedTimetableEvent;
        this.updateUnassignedEvents();
      });
    });
  }
}
