///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Params, Router} from '@angular/router';
import {AbstractControl, UntypedFormBuilder, UntypedFormGroup, Validators} from '@angular/forms';
import {PasswordValidation} from './password.validation';
import {AuthenticationService} from '../../services/authentication.service';
// import { NotificationsService } from 'angular2-notifications';
import {NotifParams} from '../entity/notifparams';
import {environment} from "../../../environments/environment";

@Component({
  selector: 'reset',
  templateUrl: './reset.html',
  styleUrls: ['./reset.scss']
})
export class Reset implements OnInit {

  protected environment = environment;
  notifParams: NotifParams = new NotifParams();
  form: UntypedFormGroup;
  confirmpassword: AbstractControl;
  password: AbstractControl;
  submitted: boolean = false;
  token: string = '';

  passwordRegex = `^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[_\\-+=<>@&’!%?$*,;:]).{10,}$`;
  constructor(private activatedRoute: ActivatedRoute, private router: Router,
    fb: UntypedFormBuilder, private resetService: AuthenticationService/*, private _service: NotificationsService*/) {
    this.form = fb.group({
      'password': ['', Validators.compose([Validators.required, Validators.pattern(this.passwordRegex)])],
      'confirmpassword': ['', Validators.compose([Validators.required])],
    }, {
        validator: PasswordValidation.MatchPassword,
      });

    this.confirmpassword = this.form.controls['confirmpassword'];
    this.password = this.form.controls['password'];
  }

  ngOnInit() {
    this.activatedRoute.params.subscribe((params: Params) => {
      const tokenparam = params['id'];
      if (tokenparam !== '' && tokenparam !== undefined) {
        this.token = tokenparam;
      } else {
        this.router.navigate(['login']);
      }
    });
  }

  onSubmit(values: Object): void {

    this.submitted = true;
    if (this.form.valid) {
      this.resetService.resetPassword(this.password.value, this.token)
        .subscribe(result => {
          window.location.assign('');
        }, err => {
          // this._service.error('Erreur', '', this.notifParams.notif);
        });
    }
  }
}
