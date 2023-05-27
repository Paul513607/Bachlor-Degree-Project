import { Component, EventEmitter, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { MatButtonToggle, MatButtonToggleChange } from '@angular/material/button-toggle';
import { AssignedTimetableEvent, emptyAssignedTimetableEvent } from 'src/app/model/assigned-timetable-event';
import { TimetableEvent } from 'src/app/model/timetable-event';

@Component({
  selector: 'app-event-card',
  templateUrl: './event-card.component.html',
  styleUrls: ['./event-card.component.css']
})
export class EventCardComponent implements OnInit, OnDestroy {
  public dayNumberToNameMap: Map<number, string> = new Map<number, string>([
    [0, 'Monday'],
    [1, 'Tuesday'],
    [2, 'Wednesday'],
    [3, 'Thursday'],
    [4, 'Friday'],
    [5, 'Saturday'],
    [6, 'Sunday']
  ]);

  @Input()
  public assignedEvent: AssignedTimetableEvent = emptyAssignedTimetableEvent;

  @Output()
  public toggleAvailabilityOn = new EventEmitter<TimetableEvent>();
  @Output()
  public toggleAvailabilityOff = new EventEmitter<TimetableEvent>();

  constructor() {
  }

  ngOnInit(): void {
  }

  ngOnDestroy(): void {
  }

  public onCloseEventCard(): void {
    this.assignedEvent = emptyAssignedTimetableEvent;
  }

  public onToggleAvailabilityCheck(toggleAvailabilityBtn: MatButtonToggle): void {
    if (this.assignedEvent.event == null) {
      return;
    }

    if (!toggleAvailabilityBtn.checked) {
      this.toggleAvailabilityOff.emit(this.assignedEvent.event);
    } else {
      this.toggleAvailabilityOn.emit(this.assignedEvent.event);
    }
  }
}
