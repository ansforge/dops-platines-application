///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

/**
 * (c) Copyright 1998-2023, ANS. All rights reserved.
 */
import {NgModule} from '@angular/core';
import {CommonModule, DatePipe} from '@angular/common';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {ProjetComponent} from './projet.component';
import {routing} from './projet.routing';
import {AppTranslationModule} from '../../app.translation.module';
import {ProjetDetailComponent} from './component/projetDetail/projetDetail.component';
import {ListProjetsComponent} from './component/listProjets/listProjets.component';
import {CreateProjetComponent} from './component/createProjet/createProjet.component';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {UIModule} from '../ui/ui.module';
import {ProjectLibraryService} from '../../services/projectlibrary.service';
import {FileService} from '../../services/file.service';
import {ProfileService} from '../../services/profile.service';
import {FilterListService} from '../../services/filterlist.service';
import {ResourcesService} from '../../services/resources.service';
import {MatTableModule} from "@angular/material/table";
import {MatPaginatorModule} from "@angular/material/paginator";
import {MatCheckboxModule} from "@angular/material/checkbox";
import {MatIconModule} from "@angular/material/icon";
import {MatSortModule} from "@angular/material/sort"
import {MatTreeModule} from "@angular/material/tree";
import {MatInputModule} from "@angular/material/input";
import {MatTabsModule} from "@angular/material/tabs";
import {MatTooltipModule} from "@angular/material/tooltip";
import {MatRadioModule} from "@angular/material/radio";
import { SharedModule } from '../../shared/shared.module';
import { ProjectTreeComponent } from './component/projetDetail/project-tree/project-tree.component';
import {MatOptionModule} from "@angular/material/core";
import {MatSelectModule} from "@angular/material/select";
import {MatButtonModule} from "@angular/material/button";
import { ProjectListTreeComponent } from './component/listProjets/project-list-tree/project-list-tree.component';
import { BulkUpdateChoiceboxComponent } from './component/listProjets/bulk-update-choicebox/bulk-update-choicebox.component';
import { BulkUpdateReportComponent } from './component/bulk-update-report/bulk-update-report.component';
import { BulkUpdateService } from './component/bulkUpdateService.service';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    routing,
    AppTranslationModule,
    UIModule,
    MatTableModule,
    MatPaginatorModule,
    MatCheckboxModule,
    MatSortModule,
    MatIconModule,
    MatTreeModule,
    MatInputModule,
    MatTabsModule,
    MatTooltipModule,
    MatRadioModule,
    SharedModule,
    MatOptionModule,
    MatSelectModule,
    MatButtonModule,
  ],
  declarations: [
    ProjetComponent,
    ProjetDetailComponent,
    ListProjetsComponent,
    CreateProjetComponent,
    ProjectTreeComponent,
    ProjectListTreeComponent,
    BulkUpdateChoiceboxComponent,
    BulkUpdateReportComponent
  ],
  exports: [
    ProjectListTreeComponent
  ],
  providers: [
    DatePipe,
    ProjectLibraryService,
    NgbModal,
    FileService,
    ProfileService,
    FilterListService,
    ResourcesService,
    BulkUpdateService
  ]
})
export class ProjetModule {}
