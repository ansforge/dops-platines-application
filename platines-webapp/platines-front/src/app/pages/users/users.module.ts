///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {AppTranslationModule} from '../../app.translation.module';
import {UsersComponent} from './users.component';
import {routing} from './users.routing';
import {UsersService} from '../../services/users.service';
// import { SimpleNotificationsModule } from 'angular2-notifications';
import {UserComponent} from './components/user/user.component';
import {ListUsersComponent} from './components/users/listusers.component';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {UIModule} from '../ui/ui.module';
import {FilterListService} from '../../services/filterlist.service';
// import {ActionsComponent} from '../users/components/users/actions.component';
import {ProjectLibraryService} from '../../services/projectlibrary.service';
import {NgMultiSelectDropDownModule} from 'ng-multiselect-dropdown';
import {MatTableModule} from "@angular/material/table";
import {MatCheckboxModule} from "@angular/material/checkbox";
import {MatInputModule} from "@angular/material/input";
import {MatPaginatorModule} from "@angular/material/paginator";
import {MatSelectModule} from "@angular/material/select";
import {MatSortModule} from "@angular/material/sort";
import {MatTooltipModule} from "@angular/material/tooltip";
import {SharedModule} from "../../shared/shared.module";

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    routing,
    AppTranslationModule,
    UIModule,
    NgMultiSelectDropDownModule.forRoot(),
    MatTableModule,
    MatCheckboxModule,
    MatInputModule,
    MatPaginatorModule,
    MatSelectModule,
    MatSortModule,
    MatTooltipModule,
    SharedModule
  ],
    declarations: [
        UsersComponent,
        UserComponent,
        ListUsersComponent,
    ],
    providers: [
        ProjectLibraryService,
        UsersService,
        NgbModal,
        FilterListService
    ]
})
export class UsersModule {}
