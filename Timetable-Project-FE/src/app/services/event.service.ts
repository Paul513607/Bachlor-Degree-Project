import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, map } from 'rxjs';
import { TimetableEvent } from '../model/timetable-event';
import { environment } from 'src/enviroments/envoriment';

@Injectable({
  providedIn: 'root'
})
export class EventService {
  constructor(private readonly _http: HttpClient) { }

  public getAllEvents(): Observable<TimetableEvent[]> {
    let url: string = `${environment.BASE_URL}:${environment.PORT}/api/events`;
    return this._http.get(url)
    .pipe(
      map<any, TimetableEvent[]>(response => {
        return response;
      })
    );
  }

  public getAllUnassignedEvents(): Observable<TimetableEvent[]> {
    let url: string = `${environment.BASE_URL}:${environment.PORT}/api/events/unassigned`;
    return this._http.get(url)
    .pipe(
      map<any, TimetableEvent[]>(response => {
        return response;
      })
    );
  }
}
