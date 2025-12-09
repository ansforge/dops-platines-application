///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';

import {routing} from './ui.routing';
import {AppTranslationModule} from '../../app.translation.module';
import {NgbModal, NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {ConfirmModalComponent} from './components/confirmModal/confirmModal.component';
import {ModalPasswordComponent} from './components/modalpassword/modalpassword.component';
import {UIComponent} from './ui.component';
import {ProjectsModalComponent} from './components/projectsModal/projectsModal.component';
import { InfoModalComponent } from './components/info-modal/info-modal.component';

@NgModule({
    imports: [
        CommonModule,
        FormsModule,
        routing,
        AppTranslationModule,
        NgbModule,
    ],
    declarations: [
        ConfirmModalComponent,
        ModalPasswordComponent,
        UIComponent,
        ProjectsModalComponent,
        InfoModalComponent,
    ],
    providers: [
        NgbModal,
    ]
})
export class UIModule {}
