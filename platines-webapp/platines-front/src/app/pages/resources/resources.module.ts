///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {NgModule} from '@angular/core';
import {CommonModule, DatePipe} from '@angular/common';
import {FormsModule as AngularFormsModule, ReactiveFormsModule} from '@angular/forms';
import {AppTranslationModule} from '../../app.translation.module';
import {NgbModal, NgbRatingModule} from '@ng-bootstrap/ng-bootstrap';
import {ListResourcesComponent} from './components/listResources/listResources.component';
import {routing} from './resources.routing';
import {FileService} from '../../services/file.service';
// import {ThemeModule} from '../../theme/theme.module';
import {ProjectLibraryService} from '../../services/projectlibrary.service';
import {NgxFileDropModule} from 'ngx-file-drop';
// import {SelectResourceTypeComponent} from './components/listResources/selectResourceType.component';
import {UIModule} from '../ui/ui.module';
import {ResourcesComponent} from './resources.component';
import {ResourcesService} from '../../services/resources.service';
import {FilterListService} from '../../services/filterlist.service';
import {MatSelectModule} from "@angular/material/select";
import {MatTableModule} from "@angular/material/table";
import {MatTabsModule} from "@angular/material/tabs";
import {MatIconModule} from "@angular/material/icon";
import {MatCheckboxModule} from "@angular/material/checkbox";
import {MatTreeModule} from "@angular/material/tree";
import {MatButtonModule} from "@angular/material/button";
import {SharedModule} from "../../shared/shared.module";
import {ProjetModule} from "../projet/projet.module";

@NgModule({
  imports: [
    CommonModule,
    routing,
    AngularFormsModule,
    AppTranslationModule,
    ReactiveFormsModule,
    NgbRatingModule,
    NgxFileDropModule,
    UIModule,
    MatSelectModule,
    MatTableModule,
    MatTabsModule,
    MatIconModule,
    MatCheckboxModule,
    MatTreeModule,
    MatButtonModule,
    SharedModule,
    ProjetModule,
  ],
    declarations: [
        ResourcesComponent,
        ListResourcesComponent,
    ],
    providers: [
        ProjectLibraryService,
        ResourcesService,
        DatePipe,
        NgbModal,
        FileService,
        FilterListService
    ]
})
export class ResourcesModule {}
