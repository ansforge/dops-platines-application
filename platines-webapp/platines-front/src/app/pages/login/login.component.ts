///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {Component} from '@angular/core';
import {Router} from '@angular/router';
import {AbstractControl, UntypedFormBuilder, UntypedFormGroup, Validators} from '@angular/forms';
import {AuthenticationService} from '../../services/authentication.service';
import {environment} from "../../../environments/environment";
import {ProfileService} from "../../services/profile.service";

@Component({
  selector: 'login',
  templateUrl: './login.html',
  styleUrls: ['./login.scss']
})
export class LoginComponent {

  protected environment = environment;
  public form: UntypedFormGroup;
  public email: AbstractControl;
  public password: AbstractControl;
  public submitted: boolean = false;
  error;

  constructor(private router: Router,
              private fb: UntypedFormBuilder,
              private authenticationService: AuthenticationService,
              private profileService: ProfileService) {

  }

  ngOnInit() {
    this.form = this.fb.group({
      'email': ['', Validators.compose([Validators.required, Validators.minLength(4)])],
      'password': ['', Validators.compose([Validators.required, Validators.minLength(4)])]
    });

    this.email = this.form.controls['email'];
    this.password = this.form.controls['password'];
  }

  public onSubmit(values: Object): void {
    this.submitted = true;
    if (this.form.valid) {
        this.authenticationService.login(this.email.value, this.password.value).subscribe(res => {
          if(res.success){
            this.profileService.setUsername(this.profileService.getIssFromToken());
            if(res.fromUrl){
              this.router.navigateByUrl(res.fromUrl);
            }else{
              this.router.navigate(['pages/accueil']);
            }
          }else{
            this.error=res.err;
          }
        }, err => {
          /*
          * FIXME : OK, c'est simple, mais si l'utilisateur entre des creds corrects 
          * et qu'il y a un problème technique, il va croire qu'il s'est planté.
          * UX médiocre...
          */
          this.error = 'Compte/mot de passe invalide';
        })
    }
  }

}
