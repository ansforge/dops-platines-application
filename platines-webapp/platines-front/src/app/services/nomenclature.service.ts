///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {Injectable} from "@angular/core";
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Observable} from 'rxjs';
import {environment} from "../../environments/environment";
import {ProfileService} from './profile.service';

@Injectable()
export class NomenclatureService {

    constructor(private http: HttpClient, private profileService: ProfileService) {
    }
    headers: HttpHeaders;
    options;

    createHeadersMultipart() {
        this.headers = new HttpHeaders();
        this.headers = this.headers.append('enctype', 'multipart/form-data;charset=UTF-8');
    }

    getNomenclatureLast(): Observable<any> {
        return this.http.get(`${environment.API_ENDPOINT_SECURE}nomenclature/last`,{ headers: this.headers });
    }

    createNomenclature(nomenclature: FormData) {
        this.createHeadersMultipart();
        return this.http.post(`${environment.API_ENDPOINT_SECURE}nomenclature/create`, nomenclature, { headers: this.headers }).pipe();
    }
}
