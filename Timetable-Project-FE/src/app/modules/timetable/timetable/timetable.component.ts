import { Component, EventEmitter, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Observable, Subject } from 'rxjs';
import { AssignedTimetableEvent } from 'src/app/model/assigned-timetable-event';
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
  public classTimes: string[] = [];

  // public assignedEvents$: Observable<AssignedTimetableEvent[]>;
  public assignedEvents: AssignedTimetableEvent[] = [];

  public unsubscribe$: Subject<void> = new Subject<void>();

  @Input()
  public selectedAlgorithmOption: string = '';
  @Input()
  public selectedStudentGroup: string = '';

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
      this.getAssignedEventsByStudentGroup();
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

    this.timetableService.getAllAssignedEventsWithStudentGroup(this.selectedStudentGroup)
    .subscribe((assignedEvents: AssignedTimetableEvent[]) => {
      this.assignedEvents = assignedEvents;
      console.log(this.assignedEvents);
    });
  }
}
