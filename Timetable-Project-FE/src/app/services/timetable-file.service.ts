import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { TimetableFile } from '../model/timetable-file';
import { environment } from 'src/enviroments/envoriment';
import { map } from 'rxjs/operators';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class TimetableFileService {

  constructor(private readonly _http: HttpClient) { }

  public getAllFileNames(): Observable<string[]> {
    let url: string = `${environment.BASE_URL}:${environment.PORT}/api/timetable-files/names`;
    return this._http.get(url)
    .pipe(
      map<any, string[]>(response => {
        return response;
      })
    );
  }

  public uploadFile(formData: FormData): Observable<TimetableFile> {
    let url: string = `${environment.BASE_URL}:${environment.PORT}/api/timetable-files`;
    return this._http.post(url, formData)
    .pipe(
      map<any, TimetableFile>(response => {
        console.log(response);
        return response;
      })
    );
  }

  public getTimetableFileIdByName(name: string): Observable<number> {
    let url: string = `${environment.BASE_URL}:${environment.PORT}/api/timetable-files`;
    if (name) {
      url += `?name=${name}`;
    }

    return this._http.get(url)
    .pipe(
      map<any, number>(response => {
        return response;
      })
    );
  }

  public deleteFileById(id: number): Observable<void> {
    let url: string = `${environment.BASE_URL}:${environment.PORT}/api/timetable-files/${id}`;
    return this._http.delete(url)
    .pipe(
      map<any, void>(response => {
        return response;
      })
    );
  }

  public setFileAsDefault(name: string): Observable<TimetableFile> {
    let url: string = `${environment.BASE_URL}:${environment.PORT}/api/timetable-files/set`;
    if (name) {
      url += `?name=${name}`;
    }

    return this._http.post(url, null)
    .pipe(
      map<any, TimetableFile>(response => {
        return response;
      })
    );
  }
}
