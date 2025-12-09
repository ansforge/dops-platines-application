///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {Component, ElementRef, ViewChild} from '@angular/core';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'modalpassword',
  styleUrls: ['./modalpassword.scss'],
  templateUrl: './modalpassword.html',
})

export class ModalPasswordComponent {
  @ViewChild("mypassword")
    private _inputElement: ElementRef;
    @ViewChild("validate")
    private validate: ElementRef;
  modalHeader: string;
  modalContent: string;
  password: String = "";
  ngAfterViewInit() {
    this._inputElement.nativeElement.focus();
  }
  constructor(public activeModal: NgbActiveModal) {

  }
  setPassword(event: any) {
    this.password = event.target.value;
  }

  closeModal() {
    this.activeModal.close({});
  }

  analyze() {
    this.activeModal.close({ password: this.password });
  }

}
