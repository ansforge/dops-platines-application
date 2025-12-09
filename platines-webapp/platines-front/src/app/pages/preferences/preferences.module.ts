///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule as AngularFormsModule, ReactiveFormsModule} from '@angular/forms';
import {AppTranslationModule} from '../../app.translation.module';
import {NgbModal, NgbModule, NgbRatingModule} from '@ng-bootstrap/ng-bootstrap';
import {routing} from './preferences.routing';
import {PreferencesComponent,} from './preferences.component';
import {PreferencesService} from '../../services/preferences.service';
import {PreferencesModalComponent} from '../ui/components/preferencesmodal/preferencesmodal.components';
import {MatTabsModule} from "@angular/material/tabs";
import {MatTableModule} from "@angular/material/table";
import {MatButtonModule} from "@angular/material/button";
import {MatPaginatorModule} from "@angular/material/paginator";
import {MatSortModule} from "@angular/material/sort";
import {MatInputModule} from "@angular/material/input";
import {MatTooltipModule} from "@angular/material/tooltip";
import {MatIconModule} from "@angular/material/icon";
import {SharedModule} from "../../shared/shared.module";

@NgModule({
  imports: [
    CommonModule,
    routing,
    AngularFormsModule,
    AppTranslationModule,
    ReactiveFormsModule,
    NgbRatingModule,
    NgbModule,
    MatTabsModule,
    MatTableModule,
    MatButtonModule,
    MatPaginatorModule,
    MatSortModule,
    MatInputModule,
    MatTooltipModule,
    MatIconModule,
    SharedModule
  ],
  declarations: [
    PreferencesComponent,
    PreferencesModalComponent,
  ],
  providers: [PreferencesService, NgbModal]
})
export class PreferencesModule {
}
