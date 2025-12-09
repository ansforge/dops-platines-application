///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {Component, OnInit, ViewChild} from '@angular/core';
import {ProjectLibraryService} from '../../../../services/projectlibrary.service';
import {ActivatedRoute, Router} from '@angular/router';
// import { NotificationsService } from 'angular2-notifications';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {Theme} from '../../../entity/theme';
import {System} from '../../../entity/system';
import {NotifParams} from '../../../entity/notifparams';
import {UsersService} from '../../../../services/users.service';
import {ProfileService} from '../../../../services/profile.service';
import {User} from '../../../entity/user';

@Component({
  selector: 'Systeme',
  templateUrl: './systeme.html',
  styleUrls: ['./systeme.scss'],
})
export class SystemeComponent implements OnInit {

  @ViewChild("systemForm")
  systemForm;
  notifParams = new NotifParams();
  user : User = new User();
  isNew: boolean = true;
  id: number;
  families: Theme[];
  system: System = new System();


  constructor(private userService: UsersService, private projectLibraryService: ProjectLibraryService, private router: Router,
    private route: ActivatedRoute, private modalService: NgbModal, private profileService: ProfileService
    /*private _service: NotificationsService*/) {
      this.system.theme = new Theme();
  }

  ngOnInit() {
    this.route
      .queryParams
      .subscribe(params => {
        if (params['id'] !== undefined) {
          this.isNew = false;
          this.id = params['id'];
          this.initComponent();
        } else {
          this.isNew = true;
          this.id = null;
        }
      });

    this.userService.getUserByMail(this.profileService.getJwtToken().sub).subscribe(user => {
      this.user = user;
      if (this.user.profile.label != 'admin'){
        this.families = this.user.families;
      } else {
        this.projectLibraryService.getFamilies().subscribe(res =>{
          this.families = res;
        });
      }
    });
  }

  initComponent() {
    this.projectLibraryService.getSystemById(this.id).subscribe(result => {
      this.system = result;
      this.system.theme = result.theme;
    }, err=> {
      // this._service.error(ERROR_CONSTANT.ERROR_MESSAGE_TITLE, ERROR_CONSTANT.ERROR_MESSAGE_GET_SYSTEM, this.notifParams.notif);
    });
  }


  saveSysteme(): void {
    if(!this.systemForm.invalid) {
      if(this.id !== null) {
        this.system.id = this.id;
        this.projectLibraryService.updateSystem(this.system).subscribe(res => {
          this.router.navigate(['/pages/systemes/listSystemes']);
        }, err => {
          // this._service.error(ERROR_CONSTANT.ERROR_MESSAGE_TITLE, ERROR_CONSTANT.ERROR_MESSAGE_UPDATE_SYSTEM, this.notifParams.notif);
        });
      } else {
        this.projectLibraryService.createSystem(this.system).subscribe(res => {
          this.router.navigate(['/pages/systemes/listSystemes']);
        }, err => {
          // this._service.error(ERROR_CONSTANT.ERROR_MESSAGE_TITLE, ERROR_CONSTANT.ERROR_MESSAGE_CREATE_SYSTEM, this.notifParams.notif);
        } );
      }
    }
  }


  back() {
    this.router.navigate(['/pages/systemes/listSystemes']);
  }


}
