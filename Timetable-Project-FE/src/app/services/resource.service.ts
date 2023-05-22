import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, map } from 'rxjs';
import { Resource } from '../model/resource';
import { environment } from 'src/enviroments/envoriment';

@Injectable({
  providedIn: 'root'
})
export class ResourceService {

  constructor(private readonly _http: HttpClient) { }

  public getAllRooms(): Observable<Resource[]> {
    let url: string = `${environment.BASE_URL}:${environment.PORT}/api/resources/rooms`;
    return this._http.get(url)
    .pipe(
      map<any, Resource[]>(response => {
        return response;
      })
    );
  }
}
