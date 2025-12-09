///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {   Injectable, Injector, StaticProvider } from "@angular/core";
import { NgbModal } from "@ng-bootstrap/ng-bootstrap";
import { InfoModalComponent } from "../info-modal/info-modal.component";
import { ConfirmModalComponent } from "../confirmModal/confirmModal.component";
import { HttpErrorResponse } from "@angular/common/http";
import { ComponentType } from "@angular/cdk/portal";

export interface ModalParms {
    modalHeader: string;
    modalContent: string;
}

export interface ErrorModalParms {
    modalHeader: string;
    err: HttpErrorResponse;
}

/**
 * Delegate around Ngbmodal to add specific behavior to the basic modal system.
 */
@Injectable({ providedIn: "root" })
export class PlatineModalService {
    constructor(private angularModalService: NgbModal, private injector: Injector) { }

    openConfirm(parms: ModalParms): Promise<any>{
        const modal = this.angularModalService.open(ConfirmModalComponent,{size: 'lg'});
        modal.componentInstance.modalHeader = parms.modalHeader;
        modal.componentInstance.modalContent = parms.modalContent;
        return modal.result;
    }

    openInfoModal(parms: ModalParms) {
        const modal = this.angularModalService.open(InfoModalComponent, {size: 'lg'});
        modal.componentInstance.modalHeader = parms.modalHeader;
        modal.componentInstance.modalContent = parms.modalContent;
    }

    openErrorModal(parms: ErrorModalParms){
        const errorBox = this.angularModalService.open(InfoModalComponent, { size: 'lg' });

        let errorMessage: string;
        if (parms.err.status === 401 || parms.err.status === 403) {
            errorMessage = 'Opération interdite'
        } else if (parms.err.error && parms.err.error.message) {
            errorMessage = parms.err.error.message;
        } else {
            errorMessage = parms.err.message;
        }

        errorBox.componentInstance.modalHeader = parms.modalHeader;
        errorBox.componentInstance.modalContent = errorMessage;
        errorBox.componentInstance.modalIcon = 'error';
    }

    openErrorModalWithCustomMessage(parms: ModalParms){
      const errorBox = this.angularModalService.open(InfoModalComponent, { size: 'lg' });
      errorBox.componentInstance.modalHeader = parms.modalHeader;
      errorBox.componentInstance.modalContent = parms.modalContent;
      errorBox.componentInstance.modalIcon = 'error';
    }

    /**
     * Open any component by type. 
     * This was added to be able to open local components as well as any predefined modal dialog box (@see openErrorModal).
     * with the same service.
     * @param type the component type for the modal to open.
     * @param serviceRefs services à propager au composant.
     */
    openComponentModal(type: ComponentType<any>, serviceRefs: StaticProvider[]=[]){
        this.angularModalService.open(
            type,
            {
                size: 'lg',
                injector: Injector.create({
                    providers: serviceRefs,
                    parent: this.injector,
                    name: "modal-injector-4-"+type.name
                })
            }
        );
    }
}
