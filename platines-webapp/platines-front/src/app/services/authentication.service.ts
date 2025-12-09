///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {Injectable} from '@angular/core';
import {Observable, of, throwError} from 'rxjs';
import {catchError, map} from 'rxjs/operators';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../environments/environment';
import jwt_decode from "jwt-decode";
import { Router } from '@angular/router';

export class Profile {
  auth: {authority: string}[];
  exp: number;
  iat: number;
  iss: string;
  profil: string;
  sub: string;
  theme: number[];
  jwtClaims: Object;
  encodedToken: string;
  
  constructor(token: string) {
    if(token){
      this.encodedToken=JSON.parse(token);
    }
    const source: any = jwt_decode(token);
    if(source) {
      this.jwtClaims = source;
      Object.keys(source).forEach( key => this[key]=source[key]);
    }
  }
  
  hasData(): boolean{
    return (this.auth!==null);
  }
  
  isValid(): boolean {
    return this.auth !==null && this.auth[0].authority!==null && this.expiryDelay()>0;
  }
  
  expiryDelay(){
    return this.exp!==null?this.exp*1000-new Date().getTime():0;
  }
}

/**
 * Constante ci-dessous exportée à titre provisoire.
 * TODO : supprimer l'export pour confier à 100% la gestion de cette donnée à {@link AuthenticationService}
 */
export const CURRENT_USER_KEY: string='currentUser';

@Injectable()
export class AuthenticationService {
  ROR_TENTATIVE_CONNECTION = 'ror_tentative_connexion';
  ROR_BLOCAGE_CONNECTION = 'ror_blocage_connexion';
  options = {
    position: ['bottom', 'right'],
    timeOut: 0,
    lastOnBottom: true,
  };
  notifParams = {
    showProgressBar: true,
    pauseOnHover: true,
    clickToClose: true,
    maxLength: 100,
  };

  constructor(
    private http: HttpClient,
    private router: Router
  ) {}
  
  getConnectedUserProfile(): Profile {
    const token: string=localStorage.getItem(CURRENT_USER_KEY);
    if(token){
      return new Profile(token);
    } else {
      return null;
    }
  }
  
  login(username: string, password: string): Observable<{success: boolean,err: string,fromUrl?: string}> {

    return this.http.post(`${environment.API_ENDPOINT_INSECURE}authent`, {
      login: username,
      password
    }, {responseType: 'text'})
    .pipe(
       map(
         (response: string) => {
           if(response === 'Compte verrouillé' || response === 'Compte/mot de passe invalide') {
            return {success: false,err: response};
          } else {
            localStorage.setItem(CURRENT_USER_KEY, JSON.stringify(response));
            const profile: Profile = this.getConnectedUserProfile();
            setTimeout(expireCheck,profile.expiryDelay(),this);
            return {success: true,err: '',fromUrl: localStorage.getItem('relog.fromUrl')};
          }
         }
      ), catchError(err => {
        return throwError(() => new Error(err));
      })
    );
  }

  refresh(token: string): void {
    localStorage.setItem(CURRENT_USER_KEY, JSON.stringify(token));
  }

  expire() {
    localStorage.removeItem(CURRENT_USER_KEY);
  }

  logout(): void {
    localStorage.removeItem(CURRENT_USER_KEY);
    localStorage.removeItem('username');
    localStorage.removeItem('relog.fromUrl')
  }
  
  logoutAndRedirect(): void {
    this.logout();
    window.location.assign('');
  }

  forgot(mail: string): Observable<any> {

    return this.http.post(`${environment.API_ENDPOINT_INSECURE}forgotPassword`, mail)
      .pipe(map((response: Response) => {
        return response;
      }),catchError(err => {
        return throwError(() => new Error(err));
      }));
  }

  resetPassword(password: string, token: string): Observable<any> {
    return this.http.post(`${environment.API_ENDPOINT_INSECURE}resetPassword`, {password: password, token: token})
      .pipe(map((response: Response) => {
        return response;
      }), catchError(err => {
        return throwError(() => new Error(err));
      }));
  }
  
  relogger(){
    this.router.navigateByUrl('/');
  }
  
  /**
   * Mémorisation de l'URL de retour après reconnexion. Normalement, cette méthode est appelée automatiquement
   * lors du routage (voir {@link AuthRor} par exemple). Il n'est nécessaire de l'appeler explicitement que si
   * on veut revenir vers une URL qui ne diffère de la précédente que pas les paramètres requête 
   * (la màj des paramètres de requête ne déclenche pas le routeur).
   *
   * @param url - url à mémoriser pour le retour après reconnexion.
   */
  updateRelogFromUrl(url: string){
    localStorage.setItem('relog.fromUrl',url);
  }
}

function expireCheck(service: AuthenticationService){
  const profile: Profile = service.getConnectedUserProfile();
  if(profile && profile.isValid()) {
    setTimeout(expireCheck,profile.expiryDelay()+10,service);
  }else{
    service.relogger();
  }
}
