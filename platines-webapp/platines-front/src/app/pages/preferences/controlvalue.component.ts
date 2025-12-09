///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

// import {Component, OnInit} from '@angular/core';
// import {EditService} from '../../services/edit.service';
// import {StateService} from '../../services/state.service';
// import {CustomColumnComponentBase} from './customcolumn.component';
//
// @Component({
//   template: `
//       <div *ngIf="edit | async" [ngClass]="{ 'has-danger': !isValid }">
//         <input type="text" class="form-control form-control-sm" [ngClass]="{'form-control-danger' : !isValid}" name="value" [(ngModel)]="value" required>
//       </div>
//       <span *ngIf="view | async">{{row.value}}</span>
//     `
// })
// export class ControlValueComponent extends CustomColumnComponentBase
//   implements OnInit {
//   private _value: string;
//   private _propertyValue = 'value';
//
//   get value() {
//     return this._value;
//   }
//
//   set value(value) {
//     this._value = value;
//     this.editService.setValidation(
//       this.rowId,
//       this._propertyValue,
//       this.isValid
//     );
//     this.stateService.value(this.rowId, this._propertyValue, value);
//   }
//
// get isValid(): boolean {
//   let valid = false;
//   if (this.row.key === "blocked_account_duration") {
//     valid = /^\d+$/.test(this._value);
//   } else if (this.row.key === "token_duration") {
//     valid = /^\d+$/.test(this._value);
//   } else if (this.row.key === "limit_try_authentication") {
//     valid = /^\d+$/.test(this._value);
//   } else if (this.row.key === "textHomePage") {
//     valid = !(!this._value || /^\s+$/.test(this._value));
//   } else if(this.row.key === "provisionning_timeout") {
//     valid = /^\d+$/.test(this._value);
//   } else if(this.row.key === "list_email_notification") {
//     valid = /^([a-zA-Z0-9.!#$%&’*+\/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\.[a-zA-Z0-9-]+)*;?\s*)+$/.test(this.value);
//   }
//   return valid;
// }
//
// constructor(editService: EditService, private stateService: StateService) {
//   super(editService);
// }
//
// ngOnInit() {
//   super.ngOnInit();
//   this.value = this.row.value;
//   this.edit.subscribe(e => {
//     this.value = this.row.value;
//   });
// }
//   }
