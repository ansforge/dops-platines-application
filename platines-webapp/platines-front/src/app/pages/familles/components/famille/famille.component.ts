///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {Component, OnInit, ViewChild} from '@angular/core';
import {ProjectLibraryService} from '../../../../services/projectlibrary.service';
import {Theme} from '../../../entity/theme';
import {ActivatedRoute, Router} from '@angular/router';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {NotifParams} from '../../../entity/notifparams';
import { CURRENT_USER_KEY } from '../../../../services/authentication.service';

@Component({
  selector: 'famille',
  templateUrl: './famille.html',
  styleUrls: ['./famille.scss'],
})
export class FamilleComponent implements OnInit {

  @ViewChild("familyForm")
  familyForm;
  notifParams = new NotifParams();
  user = JSON.parse(localStorage.getItem(CURRENT_USER_KEY));//TODO review this to use the Profile object from AuthenticationService
  isNew: boolean = true;
  id: number;
  family: Theme = new Theme();
  new: boolean;

  constructor(private projectLibraryService: ProjectLibraryService,
    private route: ActivatedRoute, private router: Router, private modalService: NgbModal,
    /*private _service: NotificationsService*/) {

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
        }
      });
  }

  initComponent() {
    this.projectLibraryService.getFamilyById(this.id).subscribe(result => {
      this.family = result;
    }, err => {
      // this._service.error(ERROR_CONSTANT.ERROR_MESSAGE_TITLE, ERROR_CONSTANT.ERROR_MESSAGE_GET_FAMILY, this.notifParams.notif);
    });
  }

  saveFamily() {
    if (!this.familyForm.invalid) {
      if (this.id === undefined) {
        this.projectLibraryService.createFamily(this.family).subscribe(result => {
          this.router.navigate(['/pages/familles/listFamilles']);
        }, err => {
          // this._service.error(ERROR_CONSTANT.ERROR_MESSAGE_TITLE, ERROR_CONSTANT.ERROR_MESSAGE_CREATE_FAMILY, this.notifParams.notif);
        });
      } else {
        this.family.id = this.id;
        this.projectLibraryService.updateFamily(this.family).subscribe(result => {
          this.router.navigate(['/pages/familles/listFamilles']);
        }, err=> {
          // this._service.error(ERROR_CONSTANT.ERROR_MESSAGE_TITLE, ERROR_CONSTANT.ERROR_MESSAGE_UPDATE_FAMILY, this.notifParams.notif);
        });
      }
    }
  }

 back(){
   this.router.navigate(['/pages/familles/listFamilles']);
 }


}
