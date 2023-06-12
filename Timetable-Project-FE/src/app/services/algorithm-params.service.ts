import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, map } from 'rxjs';
import { AlgorithmParams } from '../model/algorithm-params';
import { environment } from 'src/enviroments/envoriment';

@Injectable({
  providedIn: 'root'
})
export class AlgorithmParamsService {

  constructor(private readonly _http: HttpClient) { }

  public getAlgorithmParams(): Observable<AlgorithmParams> {
    let url: string = `${environment.BASE_URL}:${environment.PORT}/api/algorithm-parameters`;

    return this._http.get(url)
    .pipe(
      map<any, AlgorithmParams>(response => {
        return response;
      }
    ));
  }

  public setAlgorithmParams(algorithmParams: AlgorithmParams): Observable<AlgorithmParams> {
    // console.log(algorithmParams);
    let url: string = `${environment.BASE_URL}:${environment.PORT}/api/algorithm-parameters`;

    return this._http.put(url, algorithmParams)
    .pipe(
      map<any, AlgorithmParams>(response => {
        return response;
      }
    ));
  }

}
