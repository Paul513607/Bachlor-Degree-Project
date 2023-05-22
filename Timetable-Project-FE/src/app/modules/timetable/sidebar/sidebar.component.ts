import { AfterViewInit, Component, ElementRef, EventEmitter, Input, OnDestroy, OnInit, Output, ViewChild } from '@angular/core';
import { MatSelect } from '@angular/material/select';
import { ActivatedRoute, Router } from '@angular/router';
import { Subject } from 'rxjs';
import { Professor } from 'src/app/model/professor';
import { Resource } from 'src/app/model/resource';
import { StudentGroup } from 'src/app/model/student-group';
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
  ];

  public selectedQueryOption: string = 'optionStudent';

  public studentGroupList: StudentGroup[] = [];
  public professorList: Professor[] = [];
  public roomList: Resource[] = [];
  
  public studentGroupDisplayList: DisplayEntity[] = [];
  public professorDisplayList: DisplayEntity[] = [];
  public roomDisplayList: DisplayEntity[] = [];

  public selectedAlgorithmOption: DisplayEntity = {label: '', value: ''};

  public selectedStudentGroup: DisplayEntity = {label: '', value: ''};
  public selectedProfessor: DisplayEntity = {label: '', value: ''};
  public selectedRoom: DisplayEntity = {label: '', value: ''};

  public unsubscribe$: Subject<void> = new Subject<void>();


  @Output()
  public onChageSelectedAlgorithm = new EventEmitter<string>();
  
  @Output()
  public onChangeSelectedStudentGroup = new EventEmitter<string>();
  @Output()
  public onChangeSelectedProfessor = new EventEmitter<string>();
  @Output()
  public onChangeSelectedRoom = new EventEmitter<string>();

  @Output()
  public onClickGenerateTimetable = new EventEmitter<void>();

  constructor(
    private timetableService: TimetableService,
    private studentGroupService: StudentGroupService,
    private professorService: ProfessorService,
    private resourceService: ResourceService,
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
    this.onChageSelectedAlgorithm.emit(algorithmOption.value);
  }

  public onChangeStudentGroup(studentGroup: DisplayEntity): void {
    this.onChangeSelectedStudentGroup.emit(studentGroup.value);
  }

  public onChangeProfessor(professor: DisplayEntity): void {
    this.onChangeSelectedProfessor.emit(professor.value);
  }

  public onChangeRoom(room: DisplayEntity): void {
    this.onChangeSelectedRoom.emit(room.value);
  }

  public onGenerateTimetable(): void {
    if (this.selectedAlgorithmOption.value === '') {
      return;
    }
    this.onClickGenerateTimetable.emit();
  }

  private populateLists(): void {
    this.studentGroupService.getAllStudentGroups()
    .subscribe(studentGroups => {
      studentGroups.forEach(studentGroup => {
        this.studentGroupList.push(studentGroup);
        this.studentGroupDisplayList.push({label: studentGroup.name, value: studentGroup.abbr});
      });
    });

    this.professorService.getAllProfessors()
    .subscribe(professors => {
      professors.forEach(professor => {
        this.professorList.push(professor);
        this.professorDisplayList.push({label: professor.name, value: professor.abbr});
      });
    });

    this.resourceService.getAllRooms()
    .subscribe(rooms => {
      rooms.forEach(room => {
        this.roomList.push(room);
        this.roomDisplayList.push({label: room.name, value: room.abbr});
      });
    });
  }

  public onChangeStuff() {
    console.log(this.selectedQueryOption);
  }
}
