///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';

import {ApplicationsService} from '../../services/application.service';
import {ApplicationsComponent} from './applications.component';
import {routing} from './applications.routing';
import {AppTranslationModule} from '../../app.translation.module';
import {ApplicationComponent} from './components/application/application.component';
import {ListApplicationsComponent} from './components/listApplications/listApplications.component';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {UIModule} from '../ui/ui.module';
import {ChainesService} from '../../services/chaines.service';
import {ProfileService} from '../../services/profile.service';
import {FilterListService} from '../../services/filterlist.service';
import {MatTableModule} from "@angular/material/table";
import {MatInputModule} from "@angular/material/input";
import {MatIconModule} from "@angular/material/icon";
import {MatCheckboxModule} from "@angular/material/checkbox";
import {MatSortModule} from "@angular/material/sort";
import {MatPaginatorModule} from "@angular/material/paginator";
import {MatRadioModule} from "@angular/material/radio";
import {MatTooltipModule} from "@angular/material/tooltip";
import {SharedModule} from "../../shared/shared.module";

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    routing,
    AppTranslationModule,
    UIModule,
    SharedModule,
    MatTableModule,
    MatInputModule,
    MatIconModule,
    MatCheckboxModule,
    MatSortModule,
    MatPaginatorModule,
    MatRadioModule,
    MatTooltipModule
  ],
  declarations: [
    ApplicationsComponent,
    ApplicationComponent,
    ListApplicationsComponent,
  ],
  providers: [
    ApplicationsService,
    NgbModal,
    ChainesService,
    ProfileService,
    FilterListService
  ]
})
export class ApplicationsModule {
}
