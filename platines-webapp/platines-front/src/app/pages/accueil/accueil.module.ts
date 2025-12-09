///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';

import {HomeService} from '../../services/home.service';
import {ProfileService} from '../../services/profile.service';
import {routing} from './accueil.routing';
import {AppTranslationModule} from '../../app.translation.module';

import {AccueilComponent} from './accueil.component';
import {SafeHtmlPipe} from './safehtmlpipe.component';
import {CKEditorModule} from "@ckeditor/ckeditor5-angular";

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    routing,
    AppTranslationModule,
    CKEditorModule,
  ],
    declarations: [
        AccueilComponent,
        SafeHtmlPipe,
    ],
    providers: [
        HomeService,
        ProfileService,
    ]
})
export class AccueilModule {}
