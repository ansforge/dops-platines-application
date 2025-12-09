///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

// import {Component} from '@angular/core';
// import {GenericTableComponent, GtCustomComponent, GtRow} from '@angular-generic-table/core';
//
// export interface Row extends GtRow {
//     cle: string;
//     valeur: string;
//     description: string;
//     propertyType: String;
// }
//
// @Component({
//     template: `
//     <select class="form-control" [(ngModel)]="propertyType" required (change)="changeProperty()">
//     <option *ngFor="let PROPERTYTYPE of propertyTypes" [ngValue]="PROPERTYTYPE.key" [selected]="PROPERTYTYPE.key == propertyType" required>{{PROPERTYTYPE.value}}
//     </option>
//   </select>
//   <span class="text-danger" style="font-size:12px!important" *ngIf="propertyType===''">Choisir le type du paramètre</span>
//     `
// })
// export class SelectPropertyComponent extends GtCustomComponent<Row> {
//     propertyTypes = [{
//         key: 'NON_EDITABLE_VISIBLE',
//         value: 'Non éditable visible'
//
//     }, {
//         key: 'EDITABLE_VISIBLE',
//         value: 'Editable visible'
//     }, {
//         key: 'NON_EDITABLE_INVISIBLE',
//         value: 'Non éditable invisible'
//     }, {
//         key: 'ENDPOINT',
//         value: 'Endpoint'
//     }];
//
//     constructor(private table: GenericTableComponent<any, any>) {
//         super();
//     }
//
//     private _propertyType: String;
//
//     get propertyType(): String {
//         return this._propertyType;
//     }
//
//     set propertyType(value: String) {
//         this._propertyType = value;
//     }
//     changeProperty() {
//         this.row.propertyType = this._propertyType;
//     }
//     ngOnInit() {
//         this._propertyType = this.row.propertyType;
//     }
//
// }
//
