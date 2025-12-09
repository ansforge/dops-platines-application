///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {environment} from '../../../environments/environment';

@Injectable()
export class ResetService {

    constructor(private http: HttpClient) {

    }

    reset(pwd: string , accestoken: string): Observable<any> {
        return this.http.post(environment.API_ENDPOINT_INSECURE + 'reset', { password : pwd , token: accestoken  } );
    }

}
