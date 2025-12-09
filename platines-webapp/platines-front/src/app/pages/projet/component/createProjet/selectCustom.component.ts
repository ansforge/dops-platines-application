///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

// import {Component} from '@angular/core';
// import {GenericTableComponent, GtCustomComponent, GtRow} from '@angular-generic-table/core';
//
// export interface RowFile extends GtRow {
//   fileName: String;
//   fileType: string;
//   checkbox;
// }
// @Component({
//     template: `
//       <select class="form-control" [(ngModel)]="filetype" required (change)="onChange()">
//       <option *ngFor="let FILETYPE of filetypes" [ngValue]="FILETYPE.key" [selected]="FILETYPE.key === filetype" required>{{FILETYPE.value}}</option>
//       </select>
//     `
//   })
//   export class SelectCustomComponent extends GtCustomComponent<RowFile> {
//
//     filetypes = [{
//       key: 'RESOURCE',
//       value: 'Ressource'
//     }, {
//       key: 'DOCUMENT',
//       value: 'Documentation'
//     }, {
//       key: 'SINGLE_JAR',
//       value: 'Single jar'
//     }, {
//       key: 'BUNDLE_JAR',
//       value: 'Bundle jar'
//     }];
//
//     constructor(private table: GenericTableComponent<any, any>) {
//       super();
//     }
//
//     private _filetype: string;
//
//     get filetype(): string {
//       return this._filetype;
//     }
//
//     set filetype(value: string) {
//       this._filetype = value;
//     }
//     onChange() {
//       this.row.fileType = this._filetype;
//     }
//     ngOnInit() {
//       this._filetype = this.row.fileType;
//     }
//
//   }
//
