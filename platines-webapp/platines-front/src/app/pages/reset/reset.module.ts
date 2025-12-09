///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {AppTranslationModule} from '../../app.translation.module';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {Reset} from './reset.component';
import {routing} from './reset.routing';
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
    Reset,
  ],
  providers: [
    AuthenticationService,
  ],
})
export class ResetModule {
}
