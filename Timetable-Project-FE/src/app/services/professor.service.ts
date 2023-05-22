import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, map } from 'rxjs';
import { Professor } from '../model/professor';
import { environment } from 'src/enviroments/envoriment';

@Injectable({
  providedIn: 'root'
})
export class ProfessorService {

  constructor(private readonly _http: HttpClient) { }

  getAllProfessors(): Observable<Professor[]> {
    let url: string = `${environment.BASE_URL}:${environment.PORT}/api/professors`;
    return this._http.get(url)
    .pipe(
      map<any, Professor[]>(response => {
        return response;
      })
    );
  }
}
