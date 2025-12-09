///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {Injectable} from "@angular/core";
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Observable} from 'rxjs';
import {environment} from "../../environments/environment";
import {ProfileService} from './profile.service';
import {Resource} from "../pages/entity/resource";

@Injectable()
export class ResourcesService {

  headers: HttpHeaders;
  options;

  constructor(private http: HttpClient, private profileService: ProfileService) {
  }

  createHeadersMultipart() {
    this.headers = new HttpHeaders();
    this.headers = this.headers.append('enctype', 'multipart/form-data;charset=UTF-8');
  }

  addResource(data: FormData): Observable<any> {
    this.createHeadersMultipart();
    return this.http.post(`${environment.API_ENDPOINT_SECURE}resource/add`, data, {headers: this.headers}).pipe();
  }

  fetchResourcesByAssociation(associationId): Observable<Array<Resource>> {
    return this.http.get<Array<Resource>>(`${environment.API_ENDPOINT_SECURE}resource/getByAssociation/${associationId}`);
  }

  fetchResourcesByVersion(versionId): Observable<Array<Resource>> {
    return this.http.get<Array<Resource>>(`${environment.API_ENDPOINT_SECURE}resource/getByVersion/${versionId}`);
  }

  fetchAllResources(): Observable<File[]> {
    return this.http.get<File[]>(`${environment.API_ENDPOINT_SECURE}resource/getAll`, {headers: this.headers});
  }

  updateResource(resource: Resource) {
    return this.http.post(`${environment.API_ENDPOINT_SECURE}resource/update`, resource)
      // .subscribe(
      //   (res: Response) => {
      //     return res;
      //   },
      //   (err) => {
      //     return throwError(() => new Error(err));
      //   }
      // );
    //TODO: verify proper functioning
  }

  deleteResource(resource: Resource) {
    return this.http.post(`${environment.API_ENDPOINT_SECURE}resource/delete`, resource)
  }
}
