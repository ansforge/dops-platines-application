///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

// import {Component} from '@angular/core';
//
// export interface Row{
//     key: string;
//     value: String;
//     description: String;
//     propertyType: String;
// }
//
// @Component({
//     template: `
//     <input type="text" class="form-control" [(ngModel)]="valeur" name="valeur" (change)="changeValeur()" required />
//     `
// })
// export class PropertyValueComponent extends GtCustomComponent<Row> {
//
//     constructor(private table: GenericTableComponent<any, any>) {
//         super();
//     }
//
//     private _value: String;
//
//     get valeur(): String {
//         return this._value;
//     }
//
//     set valeur(value: String) {
//         this._value = value;
//     }
//     changeValeur() {
//         this.row.value = this._value;
//       }
//     ngOnInit() {
//         this._value = this.row.value;
//     }
//
// }
//
