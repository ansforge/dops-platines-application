///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {NgModule} from "@angular/core";
import {AppTreeComponent} from "./app-tree/app-tree.component";
import {MatTreeModule} from "@angular/material/tree";
import {CommonModule} from "@angular/common";
import {AppTranslationModule} from "../app.translation.module";
import {MatButtonModule} from "@angular/material/button";
import {FooterComponent} from "./footer/footer.component";
import {MatIconModule} from "@angular/material/icon";
import {MatTooltipModule} from "@angular/material/tooltip";
import {AnsPaginatorStyleDirective} from "./mat-paginator/ans-paginator-style.directive";

@NgModule({
  declarations: [
    AppTreeComponent,
    FooterComponent,
    AnsPaginatorStyleDirective
  ],
  imports: [
    CommonModule,
    MatTreeModule,
    AppTranslationModule,
    MatButtonModule,
    MatIconModule,
    MatTooltipModule
  ],
  exports: [
    AppTreeComponent,
    FooterComponent,
    AnsPaginatorStyleDirective
  ]
})
export class SharedModule {
}
