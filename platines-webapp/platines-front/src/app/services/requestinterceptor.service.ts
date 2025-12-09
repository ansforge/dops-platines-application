///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {Injectable} from '@angular/core';
import {
  HttpErrorResponse,
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest,
  HttpResponse
} from '@angular/common/http';
import {ProfileService} from './profile.service';
import {Observable, of, throwError} from 'rxjs';
import {Router} from '@angular/router';
import {tap} from 'rxjs/operators';
import { AuthenticationService, CURRENT_USER_KEY, Profile } from './authentication.service';

@Injectable()
export class RequestInterceptor implements HttpInterceptor {

  constructor(
    public profileService: ProfileService, private router: Router,
    private authenticationSrv: AuthenticationService
  ) {
  }

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const profile: Profile = this.authenticationSrv.getConnectedUserProfile();
    if (profile !== null && profile.encodedToken && profile.encodedToken !== ''){
      request = request.clone({
        setHeaders: {
          Authorization: `Bearer ${profile.encodedToken}`
        },
        setParams: {
          observe: "response",
        }
      });
    }

    return next.handle(request).pipe(tap((event: HttpEvent<any>) => {
      if(localStorage.getItem(CURRENT_USER_KEY) && this.profileService.getValidationDated()-Date.now()/1000 < 0){ //TODO review this to use the Profile object from AuthenticationService
        this.authenticationSrv.expire();
        window.location.assign('');
      }
      if (event instanceof HttpResponse) {
        if (event.headers.get('Authorization') != null && event.headers.get('Authorization') != '') {
          const token = event.headers.get('Authorization');
          this.authenticationSrv.refresh(token);
        }
      }
    }, (error: any) => {
      if (error instanceof HttpErrorResponse) {
        if (error.status === 500) {
          console.log("Internal Server Error");
        } else if (error.status === 401 || error.status === 403) {
          console.log("Unauthorized");
          this.authenticationSrv.expire();
          this.router.navigate(['/']);
        }
      }
    }));
  }

  private handleAuthError(err: HttpErrorResponse): Observable<any> {
    //handle your auth error or rethrow
    if (err.status === 401 || err.status === 403) {
      // FIXME : highly dubious : 401 is NOT 403. We don't neccessarily want to disconnect a user just because the back refused an operation (403)
      //navigate /delete cookies or whatever
      //this.router.navigateByUrl(`/login`);
      // if you've caught / handled the error, you don't want to rethrow it unless you also want downstream consumers to have to handle it as well.
      return of(err.message);
    }
    return throwError(() => new Error(err.message));
  }
}
