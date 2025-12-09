///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {AppTranslationModule} from '../../app.translation.module';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
// import { NgaModule } from '../../theme/nga.module';
import {Forgot} from './forgot.component';
import {routing} from './forgot.routing';
// import { SimpleNotificationsModule } from 'angular2-notifications';
import {AuthenticationService} from '../../services/authentication.service';
import {SharedModule} from '../../shared/shared.module';

@NgModule({
  imports: [
    CommonModule,
    AppTranslationModule,
    ReactiveFormsModule,
    FormsModule,
    routing,
    SharedModule
  ],
  declarations: [
    Forgot
  ],
  providers: [
    AuthenticationService
  ]


})
export class ForgotModule {
}
