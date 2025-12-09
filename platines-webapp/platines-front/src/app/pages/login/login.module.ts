///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {AppTranslationModule} from '../../app.translation.module';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {AuthenticationService} from '../../services/authentication.service'
import {LoginComponent} from './login.component';
import {routing} from './login.routing';
import {MatMenuModule} from "@angular/material/menu";
import {SharedModule} from '../../shared/shared.module';

@NgModule({
  imports: [
    CommonModule,
    AppTranslationModule,
    ReactiveFormsModule,
    FormsModule,
    routing,
    MatMenuModule,
    SharedModule
  ],
  declarations: [
    LoginComponent
  ],
  providers: [
    AuthenticationService,
  ],
})
export class LoginModule {
}
