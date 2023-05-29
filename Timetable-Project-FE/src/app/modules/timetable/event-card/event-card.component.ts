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
  @Input()
  public isCheckingAvailability: boolean = false;

  @Output()
  public toggleAvailabilityOn = new EventEmitter<TimetableEvent>();
  @Output()
  public toggleAvailabilityOff = new EventEmitter<TimetableEvent>();
  @Output()
  public onCloseEventCardEmitter = new EventEmitter<void>();
  @Output()
  public onDeleteEventEmitter = new EventEmitter<AssignedTimetableEvent>();

  public availabilityToggleBtn: MatButtonToggle | null = document.getElementById('availabilityButton') as MatButtonToggle | null;

  constructor() {
  }

  ngOnInit(): void {
  }

  ngOnDestroy(): void {
  }

  public onCloseEventCard(): void {
    this.assignedEvent = emptyAssignedTimetableEvent;
    this.onCloseEventCardEmitter.emit();
  }

  public onClickOtherEvent(): void {
    if (this.availabilityToggleBtn == null) {
      return;
    }

    this.availabilityToggleBtn.checked = false;
    this.toggleAvailabilityOff.emit();
  }

  public onToggleAvailabilityCheck(toggleAvailabilityBtn: MatButtonToggle): void {
    if (this.assignedEvent.event == null) {
      return;
    }
    this.isCheckingAvailability = true;

    if (!toggleAvailabilityBtn.checked) {
      this.toggleAvailabilityOff.emit();
    } else {
      this.toggleAvailabilityOn.emit(this.assignedEvent.event);
    }
  }

  public onDeleteEvent(): void {
    this.onDeleteEventEmitter.emit(this.assignedEvent);
  }
}
