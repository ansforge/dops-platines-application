///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {Component, ElementRef, ViewChild} from '@angular/core';
import {NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";

@Component({
  selector: 'app-info-modal',
  templateUrl: './info-modal.component.html',
  styleUrls: ['./info-modal.component.scss']
})
export class InfoModalComponent {

  modalHeader: string;
  modalContent: string;
  modalIcon: string;

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
}