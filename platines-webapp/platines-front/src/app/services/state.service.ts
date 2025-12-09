///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {Injectable} from '@angular/core';
import {BehaviorSubject, Observable, Subject} from 'rxjs';
import {scan} from 'rxjs/operators';

export type UpdateFunction = (dictionary: StateDictionary) => StateDictionary;
export interface StateDictionary {
    [index: string]: any;
  }

  export function deepCopy(dictionary: StateDictionary) {
    const newDictionary: StateDictionary = {};
    Object.keys(dictionary).forEach(key => {
      newDictionary[key] = {};
      Object.keys(dictionary[key]).forEach(prop => {
        newDictionary[key][prop] = dictionary[key][prop];
      });
    });
    return newDictionary;
  }
@Injectable()
export class StateService {
  private updates: Subject<UpdateFunction>;
  private _states: BehaviorSubject<StateDictionary>;

  get states(): Observable<StateDictionary> {
    return this._states.asObservable();
  }

  constructor() {
    this.updates = new Subject<UpdateFunction>();
    this._states = new BehaviorSubject<StateDictionary>({});
    this.updates
      .pipe(scan((previousState, apply: UpdateFunction) => apply(previousState), {}))
      // .do(dictionary => console.log(`State = ${JSON.stringify(dictionary, null, 2)}`))
      .subscribe(this._states);
  }

  value(rowId: string, property: string, value: any) {
    this.updates.next(dictionary => {
      const newDictionary = deepCopy(dictionary);
      if (!newDictionary[rowId]) {
        newDictionary[rowId] = {};
      }
      newDictionary[rowId][property] = value;
      return newDictionary;
    });
  }
}
