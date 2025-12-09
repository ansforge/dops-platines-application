///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {Component} from '@angular/core';
import {Router} from '@angular/router';
import {AbstractControl, UntypedFormBuilder, UntypedFormGroup, Validators} from '@angular/forms';
// import { NotificationsService } from 'angular2-notifications';
import {NotifParams} from '../entity/notifparams';
import {AuthenticationService} from '../../services/authentication.service';
import {environment} from "../../../environments/environment";
import {TranslateService} from "@ngx-translate/core";

@Component({
  selector: 'forgot',
  templateUrl: './forgot.html',
  styleUrls: ['./forgot.scss']
})
export class Forgot {

  protected environment = environment;
  form: UntypedFormGroup;
  email: AbstractControl;
  password: AbstractControl;
  submitted: boolean = false;
  notifParams: NotifParams = new NotifParams();
  error: String = '';

  constructor(private router: Router, fb: UntypedFormBuilder, private forgotService: AuthenticationService,
              private translationService: TranslateService/*,
  private _service: NotificationsService*/) {
    this.form = fb.group({
      'email': ['', Validators.compose([Validators.required, Validators.email])]

    });
    this.email = this.form.controls['email'];

  }

  public onSubmit(values: Object): void {
    this.submitted = true;
    if (this.form.valid) {
      this.forgotService.forgot(this.email.value).subscribe(result => {
        window.location.assign('');
        if(confirm("Merci de consulter votre boite mail !")) {
          this.router.navigate(['/login']);
        }
        // this._service.success('', 'Merci de consulter votre boite mail !', this.notifParams.notif);
      }, err => {
        this.error = "Cette adresse e-mail n'appartient à aucun utilisateur";
        // this._service.error('Erreur : ', `Cette adresse e-mail n'appartient à aucun utilisateur`, this.notifParams.notif);
      });
    } else {
      this.error = this.translationService.instant("pages.forgot.invalidEmail");
    }
  }

}
