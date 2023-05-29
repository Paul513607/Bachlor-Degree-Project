import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, map } from 'rxjs';
import { environment } from 'src/enviroments/envoriment';
import { AssignedTimetableEvent } from '../model/assigned-timetable-event';

@Injectable({
  providedIn: 'root'
})
export class TimetableService {
  constructor(private readonly _http: HttpClient) { }

  public getAllAssignedEventsWithAlgorithm(algorithmOption?: string): Observable<AssignedTimetableEvent[]> {
    let url: string = `${environment.BASE_URL}:${environment.PORT}/api/assigned-events/algorithm`;
    if (algorithmOption) {
      url += `?algorithmOption=${algorithmOption}`;
    }
    return this._http.get(url)
    .pipe(
      map<any, AssignedTimetableEvent[]>(response => {
        return response;
      })
    );
  }

  public getAllAssignedEventsWithStudentGroup(abbr?: string): Observable<AssignedTimetableEvent[]> {
    let url: string = `${environment.BASE_URL}:${environment.PORT}/api/assigned-events/student-group`;
    if (abbr) {
      url += `?abbr=${abbr}`;
    }

    return this._http.get(url)
    .pipe(
      map<any, AssignedTimetableEvent[]>(response => {
        return response;
      })
    );
  }

  public getAllAssignedEventsWithProfessor(abbr?: string): Observable<AssignedTimetableEvent[]> {
    let url: string = `${environment.BASE_URL}:${environment.PORT}/api/assigned-events/professor`;
    if (abbr) {
      url += `?abbr=${abbr}`;
    }

    return this._http.get(url)
    .pipe(
      map<any, AssignedTimetableEvent[]>(response => {
        return response;
      })
    );
  }

  public getAllAssignedEventsWithRoom(abbr?: string): Observable<AssignedTimetableEvent[]> {
    let url: string = `${environment.BASE_URL}:${environment.PORT}/api/assigned-events/room`;
    if (abbr) {
      url += `?abbr=${abbr}`;
    }

    return this._http.get(url)
    .pipe(
      map<any, AssignedTimetableEvent[]>(response => {
        return response;
      })
    );
  }

  public createAssignedEvent(assignedEvent: AssignedTimetableEvent): Observable<void> {
    let url: string = `${environment.BASE_URL}:${environment.PORT}/api/assigned-events`;
    return this._http.post(url, assignedEvent)
    .pipe(
      map<any, void>(response => {
        console.log(response);
        return response;
      })
    );
  }

  public updateAssignedEvent(assignedEvent: AssignedTimetableEvent): Observable<void> {
    let url: string = `${environment.BASE_URL}:${environment.PORT}/api/assigned-events`;
    return this._http.put(url, assignedEvent)
    .pipe(
      map<any, void>(response => {
        console.log(response);
        return response;
      })
    );
  }

  public getIdForAssignedEvent(assignedEvent: AssignedTimetableEvent): Observable<number> {
    let url: string = `${environment.BASE_URL}:${environment.PORT}/api/assigned-events/id`;
    return this._http.post(url, assignedEvent)
    .pipe(
      map<any, number>(response => {
        console.log(response);
        return response;
      })
    );
  }

  public deleteAssignedEvent(id: number): Observable<void> {
    let url: string = `${environment.BASE_URL}:${environment.PORT}/api/assigned-events/${id}`;
    return this._http.delete(url)
    .pipe(
      map<any, void>(response => {
        console.log(response);
        return response;
      })
    );
  }

  public downloadCurrentConfiguration(): Observable<ArrayBuffer> {
    let url: string = `${environment.BASE_URL}:${environment.PORT}/api/assigned-events/download`;
    return this._http.get(url, {responseType: 'arraybuffer'})
    .pipe(
      map<any, ArrayBuffer>(response => {
        return response;
      })
    );
  }
}
