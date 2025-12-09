///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {Injectable} from "@angular/core";
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {environment} from '../../environments/environment';
import {Router} from '@angular/router';
import {Observable} from 'rxjs';
import {Application} from '../pages/entity/application';
import {SessionList} from '../pages/entity/sessionslist';
import {Session} from "../pages/entity/session";
import {SessionDuration} from "../pages/entity/session.duration";
import {ProjectSession} from "../pages/entity/projectsession";
import {SessionDetail} from "../pages/entity/sessiondetail";
import {ProfileService} from './profile.service';
import {SessionGestion} from "../pages/entity/sessionGestion";
import {ContextStateService} from "./context-state.service";

@Injectable()
export class SessionService {

  constructor(private http: HttpClient, private router: Router, private profileService: ProfileService, private contextStateService: ContextStateService) {
  }
  headers: HttpHeaders;
  options;

  createHeadersMultipart() {
    this.headers = new HttpHeaders();
    this.headers = this.headers.append('enctype', 'multipart/form-data;charset=UTF-8');
  }

  createHeadersDownload() {
    this.headers = new HttpHeaders();
    this.headers = this.headers.append('Content-Type', 'Application/octet-stream');
  }

  getUserApplications(id: Number): Observable<Application[]> {
    return this.http.get<Application[]>(`${environment.API_ENDPOINT_SECURE}testSession/applications/${id}`);
  }


  getZipFile(idSession: Number): Observable<any> {
    this.createHeadersDownload();
    return this.http.get(`${environment.API_ENDPOINT_SECURE}testSession/logSession/${idSession}`,
      { headers: this.headers, responseType: 'blob' });
  }

  createSession(session: Session) {
    return this.http.post(`${environment.API_ENDPOINT_SECURE}testSession/create`, session);
  }

  updateSession(session: Session): Observable<any> {
    return this.http.post(`${environment.API_ENDPOINT_SECURE}testSession/update`, session);
  }

  delete(id: Number) {
    return this.http.delete(`${environment.API_ENDPOINT_SECURE}testSession/delete/${id}`);
  }

  executeSession(id: Number) {
    return this.http.get(`${environment.API_ENDPOINT_SECURE}testSession/execute/${id}`);
  }

  getSessionsDisabled(): Observable<SessionList[]> {
    return this.http.get<SessionList[]>(`${environment.API_ENDPOINT_SECURE}testSession/testSessionsListDisabled`);
  }

  getSessionsTestList(): Observable<SessionList[]> {
    return this.http.get<SessionList[]>(`${environment.API_ENDPOINT_SECURE}testSession/testSessionList`);
  }

  getSessionById(id: Number): Observable<Session> {
    return this.http.get<Session>(`${environment.API_ENDPOINT_SECURE}testSession/session/${id}`);
  }

  getSessionDetails(id: Number): Observable<SessionDetail> {
    return this.http.get<SessionDetail>(`${environment.API_ENDPOINT_SECURE}testSession/detail/${id}`);
  }

  getStepLogs(id: Number): Observable<any> {
    return this.http.get(`${environment.API_ENDPOINT_SECURE}testSession/logs/${id}`);
  }

  getStepLogsServer(id: Number): Observable<any> {
    return this.http.get(`${environment.API_ENDPOINT_SECURE}testSession/logsServer/${id}`);
  }

  getSessionDurations(): Observable<SessionDuration[]> {
    return this.http.get<SessionDuration[]>(`${environment.API_ENDPOINT_SECURE}testSession/durations`);
  }

  getProjects(idVersion: Number, role: String): Observable<ProjectSession[]> {
    return this.http.get<ProjectSession[]>(`${environment.API_ENDPOINT_SECURE}testSession/projects/${idVersion}/${role}`);
  }

  duplicateSession(session: Session): Observable<any> {
    return this.http.post(`${environment.API_ENDPOINT_SECURE}testSession/duplicate`, session);
  }

  getActiveSessions(): Observable<SessionGestion[]> {
    return this.http.get<SessionGestion[]>(`${environment.API_ENDPOINT_SECURE}testSession/allActive`);
  }

  stopSession(id: Number) {
    return this.http.get(`${environment.API_ENDPOINT_SECURE}testSession/stop/${id}`);
  }

  suppress(id: Number) {
    return this.http.delete(`${environment.API_ENDPOINT_SECURE}testSession/suppress/${id}`);
  }

  restoreSession(session: Session): Observable<any> {
    return this.http.post(`${environment.API_ENDPOINT_SECURE}testSession/restore`, session);
  }

  getROperationBySession(idSession:Number): Observable<any[]> {
    return this.http.get<any[]>(`${environment.API_ENDPOINT_SECURE}testSession/rOperation/${idSession}`);
  }

  routeToSession(value: number) {
    let session = this.contextStateService.filteredSessionList[value];
    if (session.simulatedRole === 'SERVER') {
      this.router.navigate(["/pages/sessions/sessionServeur"], {queryParams: {id: session.id}});
    } else {
      this.router.navigate(["/pages/sessions/details"], {queryParams: {id: session.id}});
    }
  }

}
