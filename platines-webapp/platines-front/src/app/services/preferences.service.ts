///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {Injectable} from "@angular/core";
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {catchError, Observable, of, Subject} from 'rxjs';
import {environment} from "../../environments/environment";
import {ProfileService} from './profile.service';
import { PlatineModalService } from "../pages/ui/components/modal/platine-modal.service";

@Injectable()
export class PreferencesService {

    constructor(
      private http: HttpClient, 
      private profileService: ProfileService,
      private modalService: PlatineModalService
    ) {}
    headers: HttpHeaders;
    options;

    updatePreferences(preferences): Observable<any> {
        return this.http.post<any>(`${environment.API_ENDPOINT_SECURE}preferences/update`, preferences)
        .pipe(
          catchError(
            err => {
              this.modalService.openErrorModal({
                err: err,
                modalHeader: 'Saisie invalide'             
              });
              return of();
            }
          )
        );
    }

    getPreferences(): Observable<any> {
        return this.http.get<any>(`${environment.API_ENDPOINT_SECURE}preferences/`);
	}

	addPreference(preference): Observable<any> {
        return this.http.post<any>(`${environment.API_ENDPOINT_SECURE}preferences/add`, preference);
    }

    private _rowIds = new Subject<string>();
	private _validations: {
		[rowId: string]: { [property: string]: boolean };
	} = {};

	get ids(): Observable<string> {
		return this._rowIds.asObservable();
	}

	public setValidation(rowId: string, property: string, isValid: boolean) {
		const validationsForRow = this._validations[rowId];
		if (!validationsForRow) {
			this._validations[rowId] = {};
		}
		this._validations[rowId][property] = isValid;
	}

	public isValid(rowId: string) {
		const validationsForRow = this._validations[rowId];
		if (!validationsForRow) {
			return true;
		}
		for (const prop in validationsForRow) {
			if (validationsForRow.hasOwnProperty(prop)) {
				if (validationsForRow[prop] === false) {
					return false;
				}
			}
		}
		return true;
	}

	toggleEditMode(rowId: string) {
		this._rowIds.next(rowId);
	}
}
