///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {NgModule} from "@angular/core";
import {CommonModule} from "@angular/common";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {routing} from './systemes.routing';
import {AppTranslationModule} from "../../app.translation.module";
// import { NgaModule } from "../../theme/nga.module";
import {UIModule} from "../ui/ui.module";
//import { SimpleNotificationsModule } from "angular2-notifications";
import {SystemesComponent} from "./systemes.component";
import {ListSystemesComponent} from "./components/listSystemes/listSystemes.component";
import {NgbModal} from "@ng-bootstrap/ng-bootstrap";
import {ProjectLibraryService} from "../../services/projectlibrary.service";
import {SystemeComponent} from "./components/systeme/systeme.component";
import {MatPaginatorModule} from "@angular/material/paginator";
import {MatIconModule} from "@angular/material/icon";
import {MatTableModule} from "@angular/material/table";
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
    MatPaginatorModule,
    MatIconModule,
    MatTableModule,
    MatInputModule,
    MatSortModule,
    MatTooltipModule,
    SharedModule
  ],
    declarations: [
        SystemesComponent,
        ListSystemesComponent,
        SystemeComponent
    ],
    providers: [
        NgbModal,
        ProjectLibraryService
    ]
})
export class SystemesModule {}
