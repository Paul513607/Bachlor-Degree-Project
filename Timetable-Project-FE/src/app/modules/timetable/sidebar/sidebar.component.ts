import { AfterViewInit, Component, EventEmitter, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Subject, delay, takeUntil } from 'rxjs';
import { AssignedTimetableEvent, emptyAssignedTimetableEvent } from 'src/app/model/assigned-timetable-event';
import { Professor } from 'src/app/model/professor';
import { Resource } from 'src/app/model/resource';
import { StudentGroup } from 'src/app/model/student-group';
import { TimetableEvent } from 'src/app/model/timetable-event';
import { EventService } from 'src/app/services/event.service';
import { ProfessorService } from 'src/app/services/professor.service';
import { ResourceService } from 'src/app/services/resource.service';
import { StudentGroupService } from 'src/app/services/student-group.service';
import { TimetableService } from 'src/app/services/timetable.service';
import { DisplayEntity } from 'src/app/util/display-entity';

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.css']
})
export class SidebarComponent implements OnInit, OnDestroy, AfterViewInit {
  public algorithmOptionList: DisplayEntity[] = [
    {label: 'Room Only Coloring', value: '1'},
    {label: 'Greedy Day-Time-Room Coloring', value: '2'},
    {label: 'DSatur Day-Time-Room Coloring', value: '3'},
    {label: 'Greedy + Hopcroft-Karp Interval Coloring', value: '4'},
    {label: 'Modified DSatur + Hopcroft-Karp Interval Coloring', value: '5'},
  ];

  public typeToTypeNameMap: Map<string, string> = new Map<string, string>([
    ['S', 'Seminar'],
    ['L', 'Laboratory'],
    ['C', 'Course'],
  ]);

  public selectedQueryOption: string = 'optionStudent';

  public studentGroupList: StudentGroup[] = [];
  public professorList: Professor[] = [];
  public roomList: Resource[] = [];
  
  public studentGroupDisplayList: DisplayEntity[] = [];
  public professorDisplayList: DisplayEntity[] = [];
  public roomDisplayList: DisplayEntity[] = [];

  public selectedAlgorithmOption: DisplayEntity = {label: '', value: ''};
  public useSorting: boolean = false;
  public shuffleEvents: boolean = false;
  public usePartialCol: boolean = false;

  public selectedStudentGroup: DisplayEntity = {label: '', value: ''};
  public selectedProfessor: DisplayEntity = {label: '', value: ''};
  public selectedRoom: DisplayEntity = {label: '', value: ''};

  @Input()
  public unassignedEvents: TimetableEvent[] = [];
  public unassignedEventsDisplay: AssignedTimetableEvent[] = [];

  @Input()
  public isDataLoading: boolean = false;

  public unsubscribe$: Subject<void> = new Subject<void>();

  @Output()
  public onChageSelectedAlgorithm = new EventEmitter<any>();
  
  @Output()
  public onChangeSelectedStudentGroup = new EventEmitter<string>();
  @Output()
  public onChangeSelectedProfessor = new EventEmitter<string>();
  @Output()
  public onChangeSelectedRoom = new EventEmitter<string>();

  @Output()
  public onClickGenerateTimetable = new EventEmitter<void>();

  @Output()
  public onClickEventCard = new EventEmitter<AssignedTimetableEvent>();

  constructor(
    private timetableService: TimetableService,
    private studentGroupService: StudentGroupService,
    private professorService: ProfessorService,
    private resourceService: ResourceService,
    private eventService: EventService,
    private router: Router,
    private readonly activatedRoute: ActivatedRoute
  ) { }

  ngAfterViewInit(): void {
  }

  ngOnInit(): void {
    this.populateLists();
  }

  ngOnDestroy(): void {
    this.unsubscribe$.next();
    this.unsubscribe$.complete();
  }

  public onChangeAlgorithmOption(algorithmOption: DisplayEntity): void {
    this.selectedAlgorithmOption = algorithmOption;
    this.onChageSelectedAlgorithm.emit({
      algorithm: algorithmOption.value,
      useSorting: this.useSorting,
      shuffleEvents: this.shuffleEvents,
      usePartialCol: this.usePartialCol
    });
  }

  public onChangeUseSorting(useSorting: boolean): void {
    this.useSorting = useSorting;
    this.onChageSelectedAlgorithm.emit({
      algorithm: this.selectedAlgorithmOption.value,
      useSorting: useSorting,
      shuffleEvents: this.shuffleEvents,
      usePartialCol: this.usePartialCol
    });
  }

  public onChangeShuffleEvents(shuffleEvents: boolean): void {
    this.shuffleEvents = shuffleEvents;
    this.onChageSelectedAlgorithm.emit({
      algorithm: this.selectedAlgorithmOption.value,
      useSorting: this.useSorting,
      shuffleEvents: shuffleEvents,
      usePartialCol: this.usePartialCol
    });
  }

  public onChangeUsePartialCol(usePartialCol: boolean): void {
    this.usePartialCol = usePartialCol;
    this.onChageSelectedAlgorithm.emit({
      algorithm: this.selectedAlgorithmOption.value,
      useSorting: this.useSorting,
      shuffleEvents: this.shuffleEvents,
      usePartialCol: usePartialCol
    });
  }

  public onChangeStudentGroup(studentGroup: DisplayEntity): void {
    this.onChangeSelectedStudentGroup.emit(studentGroup.value);
    this.updateUnassignedEvents();
  }

  public onChangeProfessor(professor: DisplayEntity): void {
    this.onChangeSelectedProfessor.emit(professor.value);
    this.updateUnassignedEvents();
  }

  public onChangeRoom(room: DisplayEntity): void {
    this.onChangeSelectedRoom.emit(room.value);
    this.updateUnassignedEvents();
  }

  public onGenerateTimetable(): void {
    if (this.selectedAlgorithmOption.value === '') {
      return;
    }
    this.onClickGenerateTimetable.emit();
    this.updateUnassignedEvents();
  }

  private updateUnassignedEvents(): void {
    this.unassignedEventsDisplay = [];
    this.eventService.getAllUnassignedEvents()
    .pipe(delay(2000))
    .subscribe(unassignedEvents => {
      unassignedEvents.forEach(unassignedEvent => {
        this.unassignedEventsDisplay.push({
          event: unassignedEvent,
          resource: null,
          day: null,
          time: null
        });
      });
      // console.log(this.unassignedEventsDisplay.length);
    });
  }

  private populateLists(): void {
    // populate the student, professor and room lists
    // for the dropdowns
    this.studentGroupService.getAllStudentGroups()
    .pipe(takeUntil(this.unsubscribe$))
    .subscribe(studentGroups => {
      studentGroups.forEach(studentGroup => {
        this.studentGroupList.push(studentGroup);
        this.studentGroupDisplayList.push({label: studentGroup.name, value: studentGroup.abbr});
      });
    });

    this.professorService.getAllProfessors()
    .pipe(takeUntil(this.unsubscribe$))
    .subscribe(professors => {
      professors.forEach(professor => {
        this.professorList.push(professor);
        this.professorDisplayList.push({label: professor.name, value: professor.abbr});
      });
    });

    this.resourceService.getAllRooms()
    .pipe(takeUntil(this.unsubscribe$))
    .subscribe((rooms: any[]) => {
      rooms.forEach(room => {
        this.roomList.push(room);
        this.roomDisplayList.push({label: room.name, value: room.abbr});
      });
    });

    // populate the unassigned events list
    // for the scroller
    this.eventService.getAllUnassignedEvents()
    .pipe(takeUntil(this.unsubscribe$))
    .subscribe(unassignedEvents => {
      unassignedEvents.forEach(unassignedEvent => {
        this.unassignedEvents.push(unassignedEvent);
        this.unassignedEventsDisplay.push({
          event: unassignedEvent,
          resource: null,
          day: null,
          time: null
        });
      });
    });
  }

  public updateCurrentEventView(currentEvent: TimetableEvent): void {
    let index = this.unassignedEvents.findIndex(event => event.abbr === currentEvent.abbr);
    if (index === -1) {
      return;
    }
    this.onClickEventCard.emit(this.unassignedEventsDisplay[index]);
  }

  public onChangeStuff() {
    // console.log(this.selectedQueryOption);
  }
}
