///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

// import {Component} from '@angular/core';
// import {GenericTableComponent, GtCustomComponent, GtRow} from '@angular-generic-table/core';
//
// export interface Row extends GtRow {
//     cle: string;
//     valeur: string;
//     description: String;
//     propertyType: String;
// }
//
// @Component({
//     template: `
//     <input type="text" class="form-control" [(ngModel)]="description" name="description" (change)="changeDescription()" required />
//     `
// })
// export class PropertyDescriptionComponent extends GtCustomComponent<Row> {
//
//     constructor(private table: GenericTableComponent<any, any>) {
//         super();
//     }
//
//     private _description: String;
//
//     get description(): String {
//         return this._description;
//     }
//
//     set description(value: String) {
//         this._description = value;
//     }
//     changeDescription() {
//         this.row.description = this._description;
//       }
//     ngOnInit() {
//         this._description = this.row.description;
//     }
//
// }
//
