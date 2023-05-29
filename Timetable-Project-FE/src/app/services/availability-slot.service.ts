import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AvailabilitySlot } from '../model/availability-slot';
import { TimetableEvent } from '../model/timetable-event';
import { Observable, map } from 'rxjs';
import { environment } from 'src/enviroments/envoriment';

@Injectable({
  providedIn: 'root'
})
export class AvailabilitySlotService {

  constructor(private readonly _http: HttpClient) { }

  public getAvailabilitySlotsByEvent(event: TimetableEvent): Observable<AvailabilitySlot[]> {
    let url: string = `${environment.BASE_URL}:${environment.PORT}/api/availability-slots`;
    return this._http.post(url, event)
    .pipe(
      map<any, AvailabilitySlot[]>(response => {
        return response;
      })
    );
  } 
}
