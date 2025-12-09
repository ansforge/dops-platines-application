///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {ChainOfTrustList} from '../pages/entity/chainoftrustlist';
import {environment} from '../../environments/environment';
import {ChainOfTrust} from '../pages/entity/chainoftrust';
import {ProfileService} from './profile.service';

@Injectable()
export class ChainesService {

    constructor(private http: HttpClient, private profileService: ProfileService) {

    }
    getAllChaines(): Observable<ChainOfTrustList[]> {

        return this.http.get<ChainOfTrustList[]>(`${environment.API_ENDPOINT_SECURE}chainOfTrust/getAll`)
    }

    getChaine(idChain: number): Observable<any> {

        return this.http.get(`${environment.API_ENDPOINT_SECURE}chainOfTrust/get/${idChain}`)
    }

    getCertificate(idCertificate: number): Observable<any> {

        return this.http.get(`${environment.API_ENDPOINT_SECURE}chainOfTrust/certificate/get/${idCertificate}`)
    }

    addChain(chainOfTrust: ChainOfTrust): Observable<any> {

        return this.http.post(`${environment.API_ENDPOINT_SECURE}chainOfTrust/add`,chainOfTrust)
    }

    updateChain(chainOfTrust: ChainOfTrust): Observable<any> {

        return this.http.post(`${environment.API_ENDPOINT_SECURE}chainOfTrust/update`,chainOfTrust);
    }

    deleteChain(idChainOfTrust: number): Observable<any> {

        return this.http.delete(`${environment.API_ENDPOINT_SECURE}chainOfTrust/delete/${idChainOfTrust}`)
    }

    addCertificate(cert: any): Observable<any> {

        return this.http.post(`${environment.API_ENDPOINT_SECURE}chainOfTrust/certificate/add`, cert)
    }

    updateCertificate(cert: any): Observable<any> {

        return this.http.post(`${environment.API_ENDPOINT_SECURE}chainOfTrust/certificate/update`, cert)
    }

    deleteCertificate(idCertificate: number): Observable<any> {

        return this.http.delete(`${environment.API_ENDPOINT_SECURE}chainOfTrust/certificate/delete/${idCertificate}`)
    }

    getChainByUser(idUser: Number): Observable<any> {

        return this.http.get(`${environment.API_ENDPOINT_SECURE}chainOfTrust/get/user/${idUser}`)
    }

}
