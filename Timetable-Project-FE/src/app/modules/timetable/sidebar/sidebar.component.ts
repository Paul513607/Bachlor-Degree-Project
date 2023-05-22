import { AfterViewInit, Component, ElementRef, EventEmitter, Input, OnDestroy, OnInit, Output, ViewChild } from '@angular/core';
import { MatSelect } from '@angular/material/select';
import { ActivatedRoute, Router } from '@angular/router';
import { Subject } from 'rxjs';
import { Professor } from 'src/app/model/professor';
import { StudentGroup } from 'src/app/model/student-group';
import { ProfessorService } from 'src/app/services/professor.service';
import { StudentGroupService } from 'src/app/services/student-group.service';
import { TimetableService } from 'src/app/services/timetable.service';

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.css']
})
export class SidebarComponent implements OnInit, OnDestroy, AfterViewInit {
  public algorithmOptionList: any[] = [
    {option: '1', label: '1'},
    {option: '2', label: '2'},
    {option: '3', label: '3'},
  ];

  public studentGroupList: StudentGroup[] = [];
  public professorList: Professor[] = [];
  
  public studentGroupAbbrsList: string[] = [];
  public professorAbbrsList: string[] = [];

  public selectedAlgorithmOption: string = '';
  public selectedStudentGroup: string = '';

  public unsubscribe$: Subject<void> = new Subject<void>();


  @Output()
  public onChageSelectedAlgorithm = new EventEmitter<string>();
  @Output()
  public onChangeSelectedStudentGroup = new EventEmitter<string>();
  @Output()
  public onClickGenerateTimetable = new EventEmitter<void>();

  constructor(
    private timetableService: TimetableService,
    private studentGroupService: StudentGroupService,
    private professorService: ProfessorService,
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

  public onChangeAlgorithmOption(algorithmOption: string): void {
    this.onChageSelectedAlgorithm.emit(algorithmOption);
  }

  public onChangeStudentGroup(studentGroup: string): void {
    this.onChangeSelectedStudentGroup.emit(studentGroup);
  }

  public onGenerateTimetable(): void {
    if (this.selectedAlgorithmOption === '') {
      return;
    }
    this.onClickGenerateTimetable.emit();
  }

  private populateLists(): void {
    this.studentGroupService.getAllStudentGroups()
    .subscribe(studentGroups => {
      studentGroups.forEach(studentGroup => {
        this.studentGroupList.push(studentGroup);
        this.studentGroupAbbrsList.push(studentGroup.abbr);
      });
    });

    this.professorService.getAllProfessors()
    .subscribe(professors => {
      professors.forEach(professor => {
        this.professorList.push(professor);
        this.professorAbbrsList.push(professor.abbr);
      });
    });
  }
}
