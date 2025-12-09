///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {VersionsComponent} from './versions.component';
import {ListVersionsComponent} from './components/listVersions/listVersions.component';
import {VersionComponent} from './components/version/version.component';
import {routing} from './versions.routing';
import {AppTranslationModule} from '../../app.translation.module';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {UIModule} from '../ui/ui.module';
import {ProjectLibraryService} from '../../services/projectlibrary.service';
import {ProfileService} from '../../services/profile.service';
import {MatTableModule} from "@angular/material/table";
import {MatIconModule} from "@angular/material/icon";
import {MatPaginatorModule} from "@angular/material/paginator";
import {MatInputModule} from "@angular/material/input";
import {MatSortModule} from "@angular/material/sort";
import {MatTooltipModule} from "@angular/material/tooltip";
import {SharedModule} from "../../shared/shared.module";

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    routing,
    AppTranslationModule,
    ReactiveFormsModule,
    UIModule,
    MatTableModule,
    MatIconModule,
    MatPaginatorModule,
    MatInputModule,
    MatSortModule,
    MatTooltipModule,
    SharedModule
  ],
  declarations: [
    VersionsComponent,
    ListVersionsComponent,
    VersionComponent,
  ],
  providers: [
   ProjectLibraryService,
   NgbModal,
   ProfileService
  ],
})
export class VersionsModule {}
