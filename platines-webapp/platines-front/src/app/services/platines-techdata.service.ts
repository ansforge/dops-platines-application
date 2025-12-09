///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable, catchError, of } from "rxjs";
import { host } from "../../environments/environment";

/**
 * Cette classe expose les données techniques platines.
 * Class créée pour afficher la version de l'application platines calculée lors du build.
 */
@Injectable({providedIn:'root'})
export class PlatinesTechDataService {
    constructor(
        private http:HttpClient
        ){}

    platinesVersion(): Observable<string> {
        return this.http.get<string>(`${host.API_ENDPOINT}techdata/public/version`).pipe(
            catchError(
                (error:any,caught: Observable<any>) => {
                    console.error("Erreur d'extraction des informations de version ",error);
                    return of('--version inconnue (erreur)--');
                }
            )
        );
    }
}