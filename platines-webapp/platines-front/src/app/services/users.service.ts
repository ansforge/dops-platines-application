///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable, throwError} from 'rxjs';
import {catchError, map} from 'rxjs/operators';
import {environment} from '../../environments/environment';
import {User} from '../pages/entity/user';
import {Identification} from '../pages/entity/identification';
import {ProfileService} from './profile.service';

@Injectable()
export class UsersService {

  response: Response;

  constructor(private http: HttpClient, private profileService: ProfileService) {
  }

  getUserById(userId: number): Observable<any> {
    return this.http.get<any>(`${environment.API_ENDPOINT_SECURE}user/get/${userId}`)
      .pipe(catchError((err) => {
        return throwError(() => new Error(err));
      }));
  }

  getUserByMail(mail: string): Observable<any> {
    return this.http.post(`${environment.API_ENDPOINT_SECURE}user/getMail/`, mail);

  }

  getAllUsers(): Observable<User[]> {
    return this.http.get<User[]>(`${environment.API_ENDPOINT_SECURE}user/getAll`)
  }

  safeFetchAllUsers(): Observable<User[]> {
    return this.http.get<User[]>(`${environment.API_ENDPOINT_SECURE}user/safeFetchAll`)
  }

  safeFetchAllUnassignedUsers(): Observable<User[]> {
    return this.http.get<User[]>(`${environment.API_ENDPOINT_SECURE}user/safeFetchAllUnassigned`)
  }

  createUser(user: User): Observable<any> {
    return this.http.post(`${environment.API_ENDPOINT_SECURE}user/add`, user).pipe(
      map((res: Response) => {
        return res;
      }), catchError((err) => {
        return throwError(() => new Error(err));
      }));
  }

  deleteUser(idUser: Number): Observable<any> {
    return this.http.delete(`${environment.API_ENDPOINT_SECURE}user/delete/${idUser}`)
      .pipe(map((res: Response) => {
        return res;
      }), catchError((err) => {
        return throwError(() => new Error(err));
      }));
    ;
  }

  updateUser(user: User): Observable<any> {
    return this.http.post(`${environment.API_ENDPOINT_SECURE}user/update`, user)
      .pipe(map((res: Response) => {
        return res;
      }), catchError((err) => {
        return throwError(() => new Error(err));
      }));
    ;
  }

  updateUserFamilies(user: User): Observable<any> {
    return this.http.post(`${environment.API_ENDPOINT_SECURE}user/updateFamilies`, user)
      .pipe(map((res: Response) => {
        return res;
      }), catchError((err) => {
        return throwError(() => new Error(err));
      }));
    ;
  }

  updateProfile(user: User, identification?: Identification): Observable<any> {
    return this.http.post(`${environment.API_ENDPOINT_SECURE}user/updateProfile`, {user, identification})
      .pipe(map((res: Response) => {
        return res;
      }));
  }
}
