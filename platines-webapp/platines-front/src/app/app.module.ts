/**
 * @license
 * Copyright Akveo. All Rights Reserved.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 * Modifications Copyright ANS 2023.
 */
import {APP_BASE_HREF} from '@angular/common';
import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {HTTP_INTERCEPTORS, HttpClientModule} from '@angular/common/http';
import {LoginModule} from './pages/login/login.module';
import {AppComponent} from './app.component';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {AuthenticationService} from './services/authentication.service';
import {ProfileService} from './services/profile.service';
import {RouterModule} from '@angular/router';
import {PagesModule} from './pages/pages.module';
import {RequestInterceptor} from './services/requestinterceptor.service';
import {AppTranslationModule} from './app.translation.module';
import {AnalyticsService} from "./core/utils/analytics.service";
import {routing} from "./app-routing.module";
import {UsersService} from "./services/users.service";
import {ForgotModule} from "./pages/forgot/forgot.module";
import {ResetModule} from "./pages/reset/reset.module";
import {FileService} from "./services/file.service";
import {MatPaginatorIntl} from "@angular/material/paginator";
import {getFrenchPaginatorIntl} from "./shared/mat-paginator/french-paginator-intl";

@NgModule({
  declarations: [AppComponent],
  imports: [
    BrowserModule,
    HttpClientModule,
    LoginModule,
    ForgotModule,
    RouterModule,
    ResetModule,
    NgbModule,
    routing,
    PagesModule,
    AppTranslationModule,
  ],
  bootstrap: [AppComponent],
  providers: [
    {provide: MatPaginatorIntl, useValue: getFrenchPaginatorIntl()},
    {provide: APP_BASE_HREF, useValue: '/'},
    AuthenticationService,
    ProfileService,
    AnalyticsService,
    UsersService,
    FileService,
    {
      provide: HTTP_INTERCEPTORS,
      useClass: RequestInterceptor,
      multi: true
    }
  ],
})
export class AppModule {
}
