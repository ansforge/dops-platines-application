///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';

import {FormsModule as AngularFormsModule, ReactiveFormsModule} from '@angular/forms';
import {AppTranslationModule} from '../../app.translation.module';
import {NgbRatingModule} from '@ng-bootstrap/ng-bootstrap';
import {ProfileComponent} from './profile.component';
import {routing} from './profile.routing';
import {UsersService} from '../../services/users.service';
import {DataService} from '../../services/data.service';
import {NgMultiSelectDropDownModule} from 'ng-multiselect-dropdown';
import {MatCheckboxModule} from "@angular/material/checkbox";
import {MatOptionModule} from "@angular/material/core";
import {MatSelectModule} from "@angular/material/select";

@NgModule({
    imports: [
        CommonModule,
        routing,
        AngularFormsModule,
        AppTranslationModule,
        ReactiveFormsModule,
        NgbRatingModule,
        NgMultiSelectDropDownModule.forRoot(),
        MatCheckboxModule,
        MatOptionModule,
        MatSelectModule,
    ],
  declarations: [
    ProfileComponent,
  ],
  providers : [
    UsersService,
    DataService,
  ]
})
export class ProfileModule {}
