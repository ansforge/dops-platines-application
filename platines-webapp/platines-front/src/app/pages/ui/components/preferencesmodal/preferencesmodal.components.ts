///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {Component, OnInit} from '@angular/core';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'preferences-modal',
  styleUrls: ['./preferencesmodal.scss'],
  templateUrl: './preferencesmodal.html',
})
export class PreferencesModalComponent implements OnInit {
  modalHeader: string;
  modalContent: string;
  properties;
  key;
  value;
  property;
  valid;
  erreur=""
  ngOnInit() {
  }

  constructor(public activeModal: NgbActiveModal) {
  }

 get isValid(): boolean {

  return this.valid;
}
  closeModal() {
    this.activeModal.dismiss();
  }

  validate() {
    if (this.key === "blocked_account_duration") {
      this.valid = /^\d+$/.test(this.value);
    } else if (this.key === "token_duration") {
      this.valid = /^\d+$/.test(this.value);
    } else if (this.key === "limit_try_authentication") {
      this.valid = /^\d+$/.test(this.value);
    } else if (this.key === "textHomePage") {
      this.valid = !(!this.value || /^\s+$/.test(this.value));
    } else if(this.key === "provisionning_timeout") {
      this.valid = /^\d+$/.test(this.value);
    } else if(this.key === "list_email_notification") {
      this.valid = /^([a-zA-Z0-9.!#$%&’*+\/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\.[a-zA-Z0-9-]+)*;?\s*)+$/.test(this.value);
    }

    if(this.valid) {
      this.activeModal.close({ key: this.key, value: this.value });
    } else {
      this.erreur = "La valeur n'est pas au format attendue";
    }
  }
  selectProperty(x) {
    this.property=this.properties.find(e=> e.key == x);
  }
}
