///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {Injectable} from "@angular/core";
import {HttpClient} from '@angular/common/http';
import {Observable, throwError} from 'rxjs';
import {environment} from '../../environments/environment';
import {FunctionalData} from "../pages/entity/functionaldata";
import {ProfileService} from './profile.service';
import {catchError} from "rxjs/operators";

@Injectable()
export class HomeService {

    constructor(private http: HttpClient, private profileService: ProfileService) {
    }

    getHomePageContent(): Observable<any> {
        return this.http.get<FunctionalData>(`${environment.API_ENDPOINT_SECURE}functionalData/homepage`)
        .pipe(catchError(err => {
          return throwError(() => new Error(err));
        }));
    }

    updateHomePageContent(content: string): Observable<any> {
        return this.http.post<FunctionalData>(`${environment.API_ENDPOINT_SECURE}functionalData/update`, content)
        .pipe(catchError(err => {
          return throwError(() => new Error(err));
        }));
      }
}
