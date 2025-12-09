///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {NgModule} from '@angular/core';
import {CommonModule, DatePipe, DecimalPipe} from '@angular/common';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {SessionComponent} from './components/session/session.component';
import {ListSessionsComponent} from './components/listSessions/listSessions.component';
import {DetailSessionComponent} from './components/detailSession/detailSession.component';
import {SessionsComponent} from './sessions.component';
import {GestionSessionComponent} from './components/gestionSession/gestionSession.component'
import {routing} from './sessions.routing';
import {AppTranslationModule} from '../../app.translation.module';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {UIModule} from '../ui/ui.module';
import {SessionServeurComponent} from './components/sessionServeur/sessionServeur.component';
import {SessionService} from '../../services/session.service';
import {UsersService} from '../../services/users.service';
import {ApplicationsService} from '../../services/application.service';
import {ProjectLibraryService} from '../../services/projectlibrary.service';
import {FileService} from '../../services/file.service';
import {ProfileService} from '../../services/profile.service';
import {FilterListService} from '../../services/filterlist.service';
import {MatTableModule} from "@angular/material/table";
import {MatCheckboxModule} from "@angular/material/checkbox";
import {MatInputModule} from "@angular/material/input";
import {MatTreeModule} from "@angular/material/tree";
import {MatIconModule} from "@angular/material/icon";
import {MatTabsModule} from "@angular/material/tabs";
import {MatButtonModule} from "@angular/material/button";
import {MatPaginatorModule} from "@angular/material/paginator";
import {MatSortModule} from "@angular/material/sort";
import {CdkDrag, CdkDropList} from "@angular/cdk/drag-drop";
import {SharedModule} from '../../shared/shared.module';
import {SessionResultTreeComponent} from './components/detailSession/session-result-tree/session-result-tree.component';
import {
  CadreGeneralElementSessionComponent
} from './components/detailSession/cadre-general-element-session/cadre-general-element-session.component';
import {MatTooltipModule} from "@angular/material/tooltip";
import {MatProgressBarModule} from "@angular/material/progress-bar";
import {MatOptionModule} from "@angular/material/core";
import {MatSelectModule} from "@angular/material/select";

@NgModule({
    imports: [
        CommonModule,
        FormsModule,
        ReactiveFormsModule,
        routing,
        AppTranslationModule,
        UIModule,
        MatTableModule,
        MatCheckboxModule,
        MatInputModule,
        MatTreeModule,
        MatIconModule,
        MatTabsModule,
        MatButtonModule,
        MatPaginatorModule,
        MatSortModule,
        CdkDropList,
        CdkDrag,
        SharedModule,
        MatTooltipModule,
        MatProgressBarModule,
        MatOptionModule,
        MatSelectModule,
    ],
  declarations: [
    SessionsComponent,
    SessionComponent,
    ListSessionsComponent,
    DetailSessionComponent,
    SessionServeurComponent,
    GestionSessionComponent,
    SessionResultTreeComponent,
    CadreGeneralElementSessionComponent,

  ],
  providers: [
    DatePipe,
    NgbModal,
    DecimalPipe,
    SessionService,
    UsersService,
    ApplicationsService,
    ProjectLibraryService,
    FileService,
    ProfileService,
    FilterListService]
})
export class SessionsModule {
}
