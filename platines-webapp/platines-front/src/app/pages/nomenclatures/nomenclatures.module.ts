///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {NgModule} from '@angular/core';
import {CommonModule, DatePipe} from '@angular/common';
import {FormsModule as AngularFormsModule, ReactiveFormsModule} from '@angular/forms';
import {AppTranslationModule} from '../../app.translation.module';
import {NgbModal, NgbRatingModule} from '@ng-bootstrap/ng-bootstrap';
import {NomenclaturesComponent} from './nomenclatures.component';
import {routing} from './nomenclatures.routing';
import {NomenclatureService} from '../../services/nomenclature.service'


@NgModule({
  imports: [
    CommonModule,
    routing,
    AngularFormsModule,
    AppTranslationModule,
    ReactiveFormsModule,
    NgbRatingModule,
  ],
  declarations: [
    NomenclaturesComponent,
  ],
  providers: [
    NomenclatureService,
    DatePipe,
    NgbModal,
  ],
})
export class NomenclaturesModule {}
