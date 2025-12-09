///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {Injectable} from '@angular/core';
import {Observable, Subject} from 'rxjs';
import {MatTableDataSource} from "@angular/material/table";
import {SelectionModel} from "@angular/cdk/collections";

@Injectable()
export class EditService {
  private _rowIds = new Subject<string>();
  private _validations: {
    [rowId: string]: { [property: string]: boolean };
  } = {};

  dataSource = new MatTableDataSource();
  selection = new SelectionModel(true, []);

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
