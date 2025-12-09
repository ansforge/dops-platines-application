///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {Component, OnInit} from '@angular/core';
import {ChainesService} from '../../../../services/chaines.service';
import {ActivatedRoute, Router} from '@angular/router';
import {AbstractControl, UntypedFormBuilder, UntypedFormGroup, Validators} from '@angular/forms';
import {Certificate} from "../../../entity/certificate";
import {ChainOfTrust} from "../../../entity/chainoftrust";

@Component({
  selector: 'certificat',
  templateUrl: './certificat.html',
  styleUrls: ['./certificat.scss'],
})
export class Certificat implements OnInit {

  form: UntypedFormGroup;
  pem: AbstractControl;
  isNew: boolean = true;
  idCdc: number;
  id: number;
  validationMessageError: string;
  cert: Certificate = new Certificate();

  constructor(public fb: UntypedFormBuilder, private route: ActivatedRoute, private router: Router,
    private _serviceChaine: ChainesService/*, private _service: NotificationsService*/) {
    this.form = this.fb.group({
      pem: ['', Validators.compose([Validators.required, Validators.minLength(4)])],
    });

  }

  ngOnInit() {
    this.route
      .queryParams
      .subscribe(params => {

        if (params['idCh'] === undefined || params['new'] === undefined || params['idCh'] === undefined) {
          this.router.navigate(['/pages/chaines/list']);
        }
        if (params['idCh'] !== undefined && params['new'] !== undefined && params['idCh'] !== undefined) {

          this.idCdc = params['idCh'];
          this.id = params['idCer'];

          if (params['new'] === 'false') {
            this.isNew = false;
            this._serviceChaine.getCertificate(this.id).subscribe(res => {
              this.form.controls['pem'].setValue(res.pem);
            })
          }
        }
      });
  }

  back() {
    this.router.navigate(['/pages/chaines/chaine'], { queryParams: { id: this.idCdc } });
  }

  addCertificate() {

    this.cert.pem = this.form.value.pem;
    this.cert.id = this.id;
    this.cert.chainOfTrust = new ChainOfTrust();
    this.cert.chainOfTrust.id = this.idCdc;

    const cert = {
      idChaine: this.idCdc,
      pem: this.form.value.pem,
      id: this.id,
    }
    if (this.id === undefined) {
      this._serviceChaine.addCertificate(this.cert).subscribe(res => {
        if (res.status) {
          this.router.navigate(['/pages/chaines/chaine'], { queryParams: { id: this.idCdc } });
        } else {
          this.validationMessageError = "Erreur lors de l'ajout du certificat";
        }
      });
    } else {
      this._serviceChaine.updateCertificate(this.cert).subscribe(res => {
        if (res.status) {
          this.router.navigate(['/pages/chaines/chaine'], { queryParams: { id: this.idCdc } });
        } else {
          this.validationMessageError = "Erreur lors de la mise à jour du certificat";
        }
      });
    }

  }


}
