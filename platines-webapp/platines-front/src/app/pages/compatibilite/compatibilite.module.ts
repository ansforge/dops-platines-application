///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule as AngularFormsModule, ReactiveFormsModule} from '@angular/forms';
import {AppTranslationModule} from '../../app.translation.module';
import {NgbRatingModule} from '@ng-bootstrap/ng-bootstrap';
import {CompatibiliteComponent} from './compatibilite.component';
import {routing} from './compatibilite.routing';
import {ProjectLibraryService} from '../../services/projectlibrary.service';
import {MatRadioModule} from "@angular/material/radio";

@NgModule({
  imports: [
    CommonModule,
    routing,
    AngularFormsModule,
    AppTranslationModule,
    ReactiveFormsModule,
    NgbRatingModule,
    MatRadioModule,
  ],
  declarations: [
    CompatibiliteComponent,
  ],
  providers: [
    ProjectLibraryService
  ],

})
export class CompatibiliteModule {}
