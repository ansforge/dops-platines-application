///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {Component, OnInit, ViewChild} from '@angular/core';
import {UntypedFormBuilder} from '@angular/forms';
import {Router} from '@angular/router';
import {User} from '../entity/user';
import {UsersService} from '../../services/users.service';
import {Identification} from '../entity/identification';
import {NotifParams} from '../entity/notifparams';
import {DataService} from '../../services/data.service';
import {ProfileService} from '../../services/profile.service';
import {Theme} from '../entity/theme';

@Component({
  selector: 'profile',
  templateUrl: './profile.html',
  styleUrls: ['./profile.scss'],
})
export class ProfileComponent implements OnInit {

  @ViewChild("profileForm")
  profileForm;

  dropdownSettings = {};
  notifParams: NotifParams = new NotifParams();
  identification: Identification = new Identification();
  lastpassword;
  confirmpassword;
  password;
  passwordRegex = `^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[_\\-+=<>@&’!%?$*,;:]).{10,}$`;
  defaultPicture = 'assets/img/theme/no-photo.png';
  user: User = new User();
  isEdit: boolean;
  mail: string;
  role: string;
  families: Theme[];
  isAdmin: boolean;
  constructor(public fb: UntypedFormBuilder, private router: Router,
    /*private _service: NotificationsService,*/ private userService: UsersService, private dataService: DataService/*, private ba : BaMsgCenter*/, private profileService: ProfileService) {

  }

  ngOnInit() {
    this.getUser();
      //this.dataService.currentMessage.subscribe(message => this.mail = message)
    this.dropdownSettings = {
      singleSelection: false,
      idField: 'id',
      textField: 'name',
      selectAllText: 'Select All',
      unSelectAllText: 'UnSelect All',
      itemsShowLimit: 5,
      enableCheckAll: false,
      allowSearchFilter: false
    };
  }
  editer() {
    this.isEdit = true;
    this.getUser();
  }
  annuler() {
    this.isEdit = false;
    this.lastpassword = null;
    this.password = null;
    this.confirmpassword = null;
    this.getUser();

  }

  getUser() {

    this.userService.getUserByMail(this.profileService.getJwtToken().sub).subscribe(res => {
      this.user = res;
      this.role = res.profile.label;
      this.families = res.families;
      this.isAdmin = res.profile.label === "admin";
    }, err => {
      // this._service.error("Erreur", "Une erreur est survenue", this.notifParams.notif);
    });

  }
  save() {
    if (!this.profileForm.invalid) {
      this.identification.lastPassword = this.lastpassword;
      this.identification.newPassword = this.password;

      this.userService.updateProfile(this.user, this.identification).subscribe(res => {
        this.mail = `${this.user.forename} ${this.user.name}`
        this.profileService.setUsername(this.mail);
        this.dataService.changeMessage(this.mail);
        // this.ba.setMail(this.mail)
        this.isEdit = false;
        this.lastpassword = null;
        this.password = null;
        this.confirmpassword = null;
        this.getUser();
        this.dataService.changeMessage("this.mail");
      }, err => {
        // // this._service.error("Erreur", "Une erreur est survenue", this.notifParams.notif);
      }
      );

    }
    // this.ba.inint = 4;


  }

}
