///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {ProjectLibraryService} from '../../services/projectlibrary.service';

import {FamillesComponent} from './familles.component';
import {routing} from './familles.routing';
import {AppTranslationModule} from '../../app.translation.module';
import {FamilleComponent} from './components/famille/famille.component';
import {ListFamillesComponent} from './components/listFamilles/listFamilles.component';
// import { NgaModule } from '../../theme/nga.module';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {UIModule} from '../ui/ui.module';
// import {SimpleNotificationsModule} from 'angular2-notifications';
import {FilterListService} from '../../services/filterlist.service';
import {UsersFamillesComponent} from './components/usersFamilles/usersFamilles.component';
// import {ActionsFamiliesComponent} from './components/listFamilles/actionsfamilies.component';
import {UsersService} from '../../services/users.service';
import {MatIconModule} from "@angular/material/icon";
import {MatInputModule} from "@angular/material/input";
import {MatTableModule} from "@angular/material/table";
import {MatPaginatorModule} from "@angular/material/paginator";
import {MatSortModule} from "@angular/material/sort";
import {MatCheckboxModule} from "@angular/material/checkbox";
import {MatTooltipModule} from "@angular/material/tooltip";
import {SharedModule} from "../../shared/shared.module";

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    routing,
    AppTranslationModule,
    UIModule,
    MatIconModule,
    MatInputModule,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    MatCheckboxModule,
    MatTooltipModule,
    SharedModule,
  ],
  declarations: [
    FamillesComponent,
    FamilleComponent,
    ListFamillesComponent,
    UsersFamillesComponent,
  ],
  providers: [
    ProjectLibraryService,
    UsersService,
    NgbModal,
    FilterListService
  ]
})
export class FamillesModule {
}
