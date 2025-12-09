///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {Component, ElementRef, ViewChild} from '@angular/core';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'confirm-modal',
  styleUrls: ['./confirmModal.scss'],
  templateUrl: './confirmModal.html',
})

export class ConfirmModalComponent {
  modalHeader: string;
  modalContent: string;
  @ViewChild("validate")
  private validate: ElementRef;

  constructor(public activeModal: NgbActiveModal) {

  }

  ngAfterViewInit() {
    this.validate.nativeElement.focus();
  }

  closeModal() {
    this.activeModal.close();
  }

  delete() {
    this.activeModal.close('confirm');

  }

}
