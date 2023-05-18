import { AfterViewInit, Component, ElementRef, EventEmitter, Input, OnDestroy, OnInit, Output, ViewChild } from '@angular/core';
import { MatSelect } from '@angular/material/select';
import { ActivatedRoute, Router } from '@angular/router';
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
  public studentGroupList: string[] = ['I3A6', 'I3B1'];

  public selectedAlgorithmOption: string = '';
  public selectedStudentGroup: string = '';


  @Output()
  public onChageSelectedAlgorithm = new EventEmitter<string>();
  @Output()
  public onChangeSelectedStudentGroup = new EventEmitter<string>();
  @Output()
  public onClickGenerateTimetable = new EventEmitter<void>();

  constructor(
    private timetableService: TimetableService,
    private router: Router,
    private readonly activatedRoute: ActivatedRoute
  ) {
  }
  ngAfterViewInit(): void {
  }

  ngOnInit(): void {
  }

  ngOnDestroy(): void {
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
}
