///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
// import { NgaModule } from '../../theme/nga.module';
import {routing} from './chaines.routing';
import {Chaines} from './chaines.component';
import {Listchaines} from './components/listchaines/listchaines.component';
import {Chaine} from './components/chaine/chaine.component';
import {Certificat} from './components/certificat/certificat.component';
import {AppTranslationModule} from '../../app.translation.module';
import {ChainesService} from '../../services/chaines.service';
// import {SimpleNotificationsModule} from 'angular2-notifications';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {UIModule} from '../ui/ui.module';
import {FilterListService} from '../../services/filterlist.service';
import {HttpClientModule} from "@angular/common/http";
import {MatTableModule} from "@angular/material/table";
import {MatCheckboxModule} from "@angular/material/checkbox";
import {MatIconModule} from "@angular/material/icon";
import {MatInputModule} from "@angular/material/input";
import {MatSortModule} from "@angular/material/sort";
import {MatPaginatorModule} from "@angular/material/paginator";
import {MatTooltipModule} from "@angular/material/tooltip";
import {SharedModule} from "../../shared/shared.module";

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    routing,
    SharedModule,
    HttpClientModule,
    AppTranslationModule,
    UIModule,
    MatTableModule,
    MatCheckboxModule,
    MatIconModule,
    MatInputModule,
    MatSortModule,
    MatPaginatorModule,
    MatTooltipModule,
  ],
  declarations: [
    Chaines,
    Listchaines,
    Chaine,
    Certificat,
  ],
  providers: [
    ChainesService,
    NgbModal,
    FilterListService
  ]
})
export class ChainesModule {
}
