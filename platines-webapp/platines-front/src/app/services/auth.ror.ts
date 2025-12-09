///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot} from '@angular/router';
import {AuthenticationService, Profile} from './authentication.service'
import {of} from 'rxjs';
import jwt_decode from "jwt-decode";
import {ProfileService} from './profile.service';

const AUTH_UNPRIVILEGED_NON_RELOGGABLE = ['application','certificat','chaine','session'];
@Injectable()
export class AuthRor implements CanActivate {

    constructor(private router: Router, private _service: AuthenticationService, private profileService: ProfileService) { }

    canActivate(next: ActivatedRouteSnapshot, state: RouterStateSnapshot)  {
        const profile: Profile = this._service.getConnectedUserProfile();
        if (profile && profile.isValid()) {
            if(relogFromUrlRule(next, AUTH_UNPRIVILEGED_NON_RELOGGABLE)) {
              this._service.updateRelogFromUrl(toURL(next));
            }
            return true;
        } else if (profile) {
            this._service.logout();
            window.location.assign('');
            return false;
        } else{
            /*
             * TODO : distinguer ce cas (accès anonyme) n'est pas forcément important en pratique, 
             * vu les effets actuels du logout
             */
            window.location.assign('');
            return false;
        }
    }
}

const POST_LOGIN_URL='/pages/accueil';
@Injectable()
export class AuthLogin implements CanActivate {

    constructor(private router: Router, private _service: AuthenticationService) { }

    //FIXME : this guard's logic is extgremely weird. It strongly warrants another study...
    canActivate(next: ActivatedRouteSnapshot, state: RouterStateSnapshot)  {
        const profile: Profile = this._service.getConnectedUserProfile();
        if (profile) {
            // logged in so return true
            this._service.updateRelogFromUrl(POST_LOGIN_URL);
            this.router.navigate([POST_LOGIN_URL]);
            return of(false);
        }
        this._service.updateRelogFromUrl(toURL(next));
        return of(true);
    }
}


const ADMIN_NON_RELOGGABLE: string[] = ['certificat']
@Injectable()
export class AuthRorAdmin implements CanActivate {

    constructor(private router: Router , private _service: AuthenticationService) { }

    canActivate(next: ActivatedRouteSnapshot, state: RouterStateSnapshot)  {
        const profile: Profile = this._service.getConnectedUserProfile();
        if (profile && profile.hasData()) {
            if ( profile.auth[0].authority === "admin" && profile.isValid()) {
                if(relogFromUrlRule(next,ADMIN_NON_RELOGGABLE)) {
                    this._service.updateRelogFromUrl(toURL(next));
                }
                return true;
            } else{
                this.router.navigate(['/pages/accueil']);
                return false;
            }
        } else if (profile) {
            this._service.logout();
            window.location.assign('');
            return false;
        } else {
            /*
             * TODO : distinguer ce cas (accès anonyme) n'est pas forcément important en pratique, 
             * vu les effets actuels du logout
             */
            window.location.assign('');
            return false;
        }
    }

}

const MANAGER_NON_RELOGGABLE = ['create','famille','systeme','user','usersFamilles','version']
@Injectable()
export class AuthRorManager implements CanActivate {

    constructor(private router: Router , private _service: AuthenticationService) { }

    canActivate(next: ActivatedRouteSnapshot, state: RouterStateSnapshot)  {
        const profile: Profile = this._service.getConnectedUserProfile();
        if (profile && profile.hasData()) {
            if ( ["admin","manager"].includes(profile.auth[0].authority)  && profile.isValid()) {
                if(relogFromUrlRule(next,MANAGER_NON_RELOGGABLE)) {
                    this._service.updateRelogFromUrl(toURL(next));
                }
                return true;
            } else {
                this.router.navigate(['/pages/accueil']);
                return false;
            }
        } else if(profile){
            this._service.logout();
            window.location.assign('');
            return false;
        } else {
            /*
             * TODO : distinguer ce cas (accès anonyme) n'est pas forcément important en pratique, 
             * vu les effets actuels du logout
             */
            window.location.assign('');
            return false;
        }
    }

}

@Injectable()
export class AuthRorAccueil implements CanActivate {
    constructor(private authentication: AuthenticationService){}
    canActivate(next: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
        this.authentication.updateRelogFromUrl(toURL(next));
        return true;
    }
}

function toURL(target: ActivatedRouteSnapshot): string{
  const url: string = target
    .pathFromRoot
    .flatMap(seg => seg.url)
    .filter(
      seg => seg.toString().trim().length>0
    ).join('/');
    
    var allQueryParmString: string;
    if(target?.queryParamMap?.keys.length>0) {
      const parmMap = target?.queryParamMap;
      allQueryParmString='?'+parmMap.keys
          .flatMap(key => { return {key:key, values:parmMap.getAll(key)}})
          .flatMap(
             tuple => tuple.values.flatMap(
               value => tuple.key+'='+value
             )
           )
          .join('&');
    }else{
      allQueryParmString='';
    }
    
    return url+allQueryParmString;
}

function relogFromUrlRule(next: ActivatedRouteSnapshot, nonReloggableSegments: string[]): boolean{
        var nbFragments=0;
        var segment=next;
        while(segment){
          segment=segment.parent;
          nbFragments++;
        }
        
        const nextSegment = next.url.flatMap(p=>p.path).join();
        if(nbFragments<3 ||nonReloggableSegments.includes(nextSegment)){
            return false;
        }else{
            return true;
        } 
}