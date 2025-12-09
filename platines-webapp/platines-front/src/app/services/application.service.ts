///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {environment} from '../../environments/environment';
import {ApplicationList} from '../pages/entity/applicationlist';
import {Application} from '../pages/entity/application';
import {ProfileService} from './profile.service';

@Injectable()
export class ApplicationsService {

    constructor(private http: HttpClient, private profileService: ProfileService) {
    }

    getApplications(): Observable<ApplicationList[]> {
        return this.http.get<ApplicationList[]>(`${environment.API_ENDPOINT_SECURE}application/getAll`);
    }

    getApplicationById(id: Number): Observable<Application> {
        return this.http.get<Application>(`${environment.API_ENDPOINT_SECURE}application/get/${id}`);
    }

    createApplication(applicationDto: Application): Observable<any> {
        return this.http.post(`${environment.API_ENDPOINT_SECURE}application/create`, applicationDto);
    }

    updateApplication(applicationDto: Application): Observable<any> {
        return this.http.post(`${environment.API_ENDPOINT_SECURE}application/update`, applicationDto);
    }

    deleteApp(applicationDto: Application): Observable<any> {
        return this.http.delete(`${environment.API_ENDPOINT_SECURE}application/delete/${applicationDto.id}`);
    }
}
