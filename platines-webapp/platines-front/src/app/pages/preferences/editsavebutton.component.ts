///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

// import {Component} from '@angular/core';
// import {PreferencesService} from '../../services/preferences.service';
// import {EditService} from '../../services/edit.service';
// import {StateService} from '../../services/state.service';
// import {CustomColumnComponentBase} from './customcolumn.component';
//
// @Component({
//     template: `
//       <button *ngIf="view | async" class="btn-table" (click)=click($event);><i class="fa fa-pencil"></i></button>
//       <button *ngIf="edit | async" class="btn-table" [disabled]="!isValid" (click)=click($event);><i class="fa fa-save"></i></button>
//     `,
//     styles: [
//       `
//     .btn.btn-sm {
//         padding-top: 0.15rem;
//         padding-bottom: 0.15rem;
//     }`
//     ]
//   })
//   export class EditSaveButtonComponent extends CustomColumnComponentBase {
//     constructor(editService: EditService, private stateService: StateService, private preferenceService: PreferencesService) {
//       super(editService);
//     }
//
//
//     public click(event: Event) {
//       if (!this.editModeActive) {
//       } else {
//         this.save();
//
//       }
//       event.stopPropagation();
//     }
//
//     public save() {
//
//     }
//   }
