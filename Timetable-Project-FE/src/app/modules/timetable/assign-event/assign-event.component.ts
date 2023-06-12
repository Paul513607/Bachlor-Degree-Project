import { Component, EventEmitter, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { FormControl } from '@angular/forms';
import { AssignedTimetableEvent, emptyAssignedTimetableEvent } from 'src/app/model/assigned-timetable-event';
import { Resource } from 'src/app/model/resource';
import { TimetableService } from 'src/app/services/timetable.service';

@Component({
  selector: 'app-assign-event',
  templateUrl: './assign-event.component.html',
  styleUrls: ['./assign-event.component.css']
})
export class AssignEventComponent implements OnInit, OnDestroy {
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

  @Input()
  public currentEventToAssign: AssignedTimetableEvent = emptyAssignedTimetableEvent;
  @Input()
  public availableRooms: Resource[] = [];
  @Input()
  public allRooms: Resource[] = [];

  @Output()
  public getTimeslotRooms = new EventEmitter<any>();

  public hour: number = 0;
  public minute: number = 0;
  public selectedDay: string = '';
  public selectedRoom: Resource | null = null;
  public selectedRoomText: string = '';

  @Input()
  public isOverlap: boolean = false;
  public isRoomInvalid: boolean = false;

  @Output()
  public onAssignEvent = new EventEmitter<any>();
  
  constructor(private timetableService: TimetableService) { }

  ngOnInit(): void {
  }

  ngOnDestroy(): void {
  }

  public getAvailableRooms(): void {
    if (this.hour == 0 || this.selectedDay == '') {
      return;
    }

    if (this.selectedRoomText != '') {
      let roomOpt : Resource | undefined = this.allRooms.find((room: Resource) => room.name == this.selectedRoomText);

      if (roomOpt != undefined) {
        this.selectedRoom = roomOpt;
        this.isRoomInvalid = false;
      } else {
        this.isRoomInvalid = true;
      }
      // console.log(this.isRoomInvalid);
    }
    this.getTimeslotRooms.emit({day: this.selectedDay, hour: this.hour, minute: this.minute});
  }

  public assignEvent(): void {
    let dayValue: number | undefined = this.daysToValues.get(this.selectedDay);
    if (this.currentEventToAssign.event == null && this.selectedRoom == null && dayValue == null) {
      return;
    }

    let dayValueAsNull: number | null = dayValue == undefined ? null : dayValue;
    let time: string;
    if (this.hour < 10) {
      time = `0${this.hour}:`
    } else {
      time = `${this.hour}:`
    }

    if (this.minute < 10) {
      time += `0${this.minute}:00`;
    } else {
      time += `${this.minute}:00`;
    }

    const newEvent: AssignedTimetableEvent = {
      event: this.currentEventToAssign.event,
      resource: this.selectedRoom,
      day: dayValueAsNull,
      time: time
    }

    this.onAssignEvent.emit(newEvent);
    this.resetData();
  }

  private resetData(): void {
    this.hour = 0;
    this.minute = 0;
    this.selectedDay = '';
    this.selectedRoom = null;
  }
}
