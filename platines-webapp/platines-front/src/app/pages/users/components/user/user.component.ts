///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {Component, ViewChild} from '@angular/core';
import {UsersService} from '../../../../services/users.service';
import {ActivatedRoute, Router} from '@angular/router';
import {ProfileService} from '../../../../services/profile.service';
import {User} from '../../../entity/user';
import {NotifParams} from '../../../entity/notifparams';
import {Profile} from '../../../entity/profile';
import {UntypedFormBuilder, UntypedFormGroup} from '@angular/forms';
import {ProjectLibraryService} from '../../../../services/projectlibrary.service';
import {Theme} from '../../../entity/theme';
import {HttpClient} from "@angular/common/http";
import {MatOption} from "@angular/material/core";
import {MatCheckbox} from "@angular/material/checkbox";
import { CURRENT_USER_KEY } from '../../../../services/authentication.service';

@Component({
  selector: 'user',
  templateUrl: './user.html',
  styleUrls: ['./user.scss'],
})
export class UserComponent {

  @ViewChild("userForm")
  userForm;
  id;
  isAdmin = false;
  dropdownSettings = {};
  themesList: Theme[];
  user: User = new User();
  notifParams: NotifParams = new NotifParams();
  form: UntypedFormGroup;
  currentUser = JSON.parse(localStorage.getItem(CURRENT_USER_KEY));//TODO review this to use the Profile object from AuthenticationService
  readOnly: boolean = false;
  profiles = [{
    id: 1,
    label: 'Administrateur',
  }, {
    id: 2,
    label: 'Éditeur',
  }, {
    id: 3,
    label: 'Gestionnaire de thème',
  }]
  allSelected: boolean;
  new: Boolean;
  protected filteredThemes: Theme[];
  @ViewChild("selectAll")
  private selectAll: MatCheckbox;

  constructor(private projectLibraryService: ProjectLibraryService, private http: HttpClient, private router: Router, private route: ActivatedRoute, private service: UsersService,
              /*private _service: NotificationsService,*/ private fb: UntypedFormBuilder, private profileService: ProfileService) {
    this.user.profile = new Profile();
    this.user.families = new Array<Theme>();
  }

  ngOnInit() {

    if (this.profileService.getProfileFromToken() === "admin") {
      this.isAdmin = true;
    }

    this.route
      .queryParams
      .subscribe(params => {
        if (params['id'] !== undefined) {
          this.new = false;
          this.id = params['id'];
          this.getUser(this.id);
        }
        else
          this.new = true;
      });

    this.projectLibraryService.getFamilies()
      .subscribe(res => {
        this.themesList = res
        this.filteredThemes = this.themesList;
      }, err => {
        // this._service.error(ERROR_CONSTANT.ERROR_MESSAGE_TITLE, ERROR_CONSTANT.ERROR_MESSAGE_ALL_FAMILIES, this.notifParams.notif);
      });

    this.dropdownSettings = {
      singleSelection: false,
      idField: 'id',
      textField: 'name',
      selectAllText: 'Select All',
      unSelectAllText: 'UnSelect All',
      itemsShowLimit: 5,
      allowSearchFilter: true
    };

  }

  compareThemes(theme1, theme2) {
    return theme1 && theme2 && theme1.id == theme2.id;
  }

  getUser(id) {
    this.service.getUserById(id).subscribe(res => {
      this.user = res;
      if (this.user.mail == this.profileService.getJwtToken().sub) {
        this.readOnly = true;
      }
    }, err => {
      // this._service.error("","Erreur de chargement de l'utilisateur", this.notifParams.notif);
    })
  }

  createUser() {
    if (!this.userForm.invalid) {

      if (this.id === undefined) {
        this.service.createUser(this.user).subscribe(res => {
            this.router.navigate(['/pages/users/listusers']);
          },
          (err) => {
            // this._service.error(`Erreur d'ajout d'utilisateur`, `Problème lors de l'ajout de cet utilisateur`, this.notifParams.notif);
          });
      } else if (this.isAdmin) {
        this.service.updateUser(this.user).subscribe(res => {
            this.router.navigate(['/pages/users/listusers']);
          },
          (err) => {
            // this._service.error(`Erreur de modification d'utilisateur`, `Problème lors de la modification des informations de cet utilisateur`, this.notifParams.notif);
          });
      } else {
        this.service.updateUserFamilies(this.user).subscribe(res => {
            this.router.navigate(['/pages/users/listusers']);
          },
          (err) => {
            // this._service.error(`Erreur de modification d'utilisateur`, `Problème lors de la modification des informations de cet utilisateur`, this.notifParams.notif);
          });
      }
    }

  }

  back() {
    this.router.navigate(['/pages/users/listusers']);
  }

  toggleSelectAll() {
    if (this.selectAll.checked) {
      this.user.families = this.filteredThemes;
    } else {
      this.user.families = [];
    }
  }

  filterThemes(value: string) {
    this.filteredThemes = this.themesList.filter(theme => theme.name.toLowerCase().includes(value.toLowerCase()));
  }
}
