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

  getAllAssignedEventsWithAlgorithm(algorithmOption?: string): Observable<AssignedTimetableEvent[]> {
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

  getAllAssignedEventsWithStudentGroup(abbr?: string): Observable<AssignedTimetableEvent[]> {
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
}
