///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {NgModule} from '@angular/core';

import {PagesComponent} from './pages.component';
import {routing} from './pages-routing.module';
import {AppTranslationModule} from '../app.translation.module';
import {AuthLogin, AuthRor, AuthRorAccueil, AuthRorAdmin, AuthRorManager} from '../services/auth.ror';
import {MatListModule} from "@angular/material/list";
import {MatIconModule} from "@angular/material/icon";
import {NgForOf, NgIf} from "@angular/common";
import {MatMenuModule} from "@angular/material/menu";
import {MatButtonModule} from "@angular/material/button";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import { SharedModule } from '../shared/shared.module';

const PAGES_COMPONENTS = [
  PagesComponent,
];

@NgModule({
  imports: [
    routing,
    // ThemeModule,
    AppTranslationModule,
    MatListModule,
    MatIconModule,
    NgIf,
    NgForOf,
    MatMenuModule,
    MatButtonModule,
    BrowserAnimationsModule,
    SharedModule
  ],
  declarations: [
    PagesComponent
  ],
  providers: [
    AuthRor,
    AuthRorAdmin,
    AuthLogin,
    AuthRorManager,
    AuthRorAccueil
],
})
export class PagesModule {
}
