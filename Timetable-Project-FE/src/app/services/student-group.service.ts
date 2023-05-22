import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { StudentGroup } from '../model/student-group';
import { Observable, map } from 'rxjs';
import { environment } from 'src/enviroments/envoriment';

@Injectable({
  providedIn: 'root'
})
export class StudentGroupService {

  constructor(private readonly _http: HttpClient) { }

  getAllStudentGroups(): Observable<StudentGroup[]> {
    let url: string = `${environment.BASE_URL}:${environment.PORT}/api/student-groups`;
    return this._http.get(url)
    .pipe(
      map<any, StudentGroup[]>(response => {
        return response;
      })
    );
  }
}
