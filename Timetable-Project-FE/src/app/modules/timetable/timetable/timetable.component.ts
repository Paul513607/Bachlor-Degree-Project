import { Component, EventEmitter, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { BehaviorSubject, Observable, Subject, lastValueFrom } from 'rxjs';
import { AssignedTimetableEvent, emptyAssignedTimetableEvent } from 'src/app/model/assigned-timetable-event';
import { TimetableService } from 'src/app/services/timetable.service';

@Component({
  selector: 'app-timetable',
  templateUrl: './timetable.component.html',
  styleUrls: ['./timetable.component.css']
})
export class TimetableComponent implements OnInit, OnDestroy {
  START_TIME: number = 8;
  END_TIME: number = 20;
  GENERAL_CLASS_DURATION: number = 2;

  public days: string[] = ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']
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

  public unsubscribe$: Subject<void> = new Subject<void>();

  @Input()
  public selectedAlgorithmOption: string = '';
  @Input()
  public selectedStudentGroup: string = '';

  public dayToEventsMap$: Observable<Map<string, AssignedTimetableEvent[]>> = new Observable<Map<string, AssignedTimetableEvent[]>>();
  public dayToEventsMap: Map<string, AssignedTimetableEvent[]> = new Map<string, AssignedTimetableEvent[]>();

  constructor(
    private timetableService: TimetableService,
    private router: Router,
    private readonly activatedRoute: ActivatedRoute
  ) {}

  ngOnInit(): void {
    for (let hour: number = this.START_TIME; hour < this.END_TIME; hour += this.GENERAL_CLASS_DURATION) {
      let classTime = hour.toString() + ":00" + "-" + (hour + 2).toString() + ":00";
      this.classTimes.push(classTime);
    }
  }

  ngOnDestroy(): void {
    this.unsubscribe$.next();
    this.unsubscribe$.complete();
  }

  public onChageSelectedAlgorithm(algorithmOption: string): void {
    this.selectedAlgorithmOption = algorithmOption;
    console.log(this.selectedAlgorithmOption);
  }

  public onChangeStudentGroup(studentGroup: string): void {
    this.selectedStudentGroup = studentGroup;

    if (this.selectedAlgorithmOption !== '') {
      this.getAssignedEventsByStudentGroup();
    }
    if (this.selectedStudentGroup !== '') {
      this.getAssignedEventsByStudentGroup();
    }
    console.log(this.selectedStudentGroup);
  }

  public onGenerateTimetable(): void {
    if (this.selectedAlgorithmOption !== '') {
      this.getAssignedEventsByAlgorithmOption();
    }
  }

  public getAssignedEventsByAlgorithmOption(): void {
    if (this.selectedAlgorithmOption === '') {
      return;
    }

    this.timetableService.getAllAssignedEventsWithAlgorithm(this.selectedAlgorithmOption)
    .subscribe((assignedEvents: AssignedTimetableEvent[]) => {
      this.assignedEvents = assignedEvents;
      console.log(this.assignedEvents);
    });
  }

  public getAssignedEventsByStudentGroup(): void {
    if (this.selectedStudentGroup === '') {
      return;
    }

    this.assignedEvents$ = this.timetableService.getAllAssignedEventsWithStudentGroup(this.selectedStudentGroup);

    this.assignedEvents$.subscribe((assignedEvents: AssignedTimetableEvent[]) => {
      this.assignedEvents = assignedEvents;
      this.dayToEventsMap$ = this.addEventsToMap(this.assignedEvents);
    });
  }

  private addEventsToMap(assignedEvents: AssignedTimetableEvent[]): Observable<Map<string, AssignedTimetableEvent[]>> {
    let dayToAssignedEventsMap: Map<string, AssignedTimetableEvent[]> = new Map<string, AssignedTimetableEvent[]>();
    console.log(assignedEvents);

    for (const day of this.days) {
      let currentDayEvents: AssignedTimetableEvent[] = [];
      for (let hour: number = this.START_TIME; hour < this.END_TIME; hour += this.GENERAL_CLASS_DURATION) {
        currentDayEvents.push(emptyAssignedTimetableEvent);
      }
      dayToAssignedEventsMap.set(day, currentDayEvents);
    }

    for (let day of this.days) {
      for (let assignedEvent of assignedEvents) {
        const dayValue = this.daysToValues.get(day);

        if (assignedEvent.day === dayValue) {
          let currentSlotIndex: number = this.getCurrentSlotIndex(day, assignedEvent.time);

          if (currentSlotIndex !== -1) {
            dayToAssignedEventsMap.get(day)![currentSlotIndex] = assignedEvent;   
          }
        }
      }
    }

    console.log(dayToAssignedEventsMap);
    return new BehaviorSubject<Map<string, AssignedTimetableEvent[]>>(dayToAssignedEventsMap).asObservable();
  }

  private getCurrentSlotIndex(day: string, time: string | null): number {
    if (time === null) {
      return -1;
    }

    let currentSlotIndex: number = 0;
    const currentHour: number = parseInt(time.split(":")[0]);
    for (let hour: number = this.START_TIME; hour < this.END_TIME; hour += this.GENERAL_CLASS_DURATION) {
      if (currentHour === hour) {
        return currentSlotIndex;
      }
      currentSlotIndex++; 
    }
    return -1;
  }
}
