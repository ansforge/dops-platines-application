///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {Component, OnInit, ViewChild} from '@angular/core';
import {ProjectLibraryService} from '../../../../services/projectlibrary.service';
import {ActivatedRoute, Router} from '@angular/router';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {Theme} from '../../../entity/theme';
import {System} from '../../../entity/system';
import {Version} from '../../../entity/version';
import {NotifParams} from '../../../entity/notifparams';
import {ProfileService} from '../../../../services/profile.service';
import { CURRENT_USER_KEY } from '../../../../services/authentication.service';


@Component({
  selector: 'version',
  templateUrl: './version.html',
  styleUrls: ['./version.scss'],
})
export class VersionComponent implements OnInit {

  @ViewChild("versionForm")
  versionForm;
  notifParams: NotifParams = new NotifParams();
  user = JSON.parse(localStorage.getItem(CURRENT_USER_KEY));//TODO review this to use the Profile object from AuthenticationService
  families: Theme[];
  version: Version = new Version();
  systems: System[];
  idVersion: Number;
  isAdmin = false;
  isNew:boolean;

  constructor(private profileService: ProfileService, private projectLibraryService: ProjectLibraryService,
    private route: ActivatedRoute, private router: Router, private modalService: NgbModal) {

      this.version.service = new System();
      this.version.service.theme = new Theme();

  }

  ngOnInit() {

    if (this.profileService.getProfileFromToken() === "admin") {
      this.isAdmin = true;
    } else {
      this.version.visibility = true;
    }

    this.projectLibraryService.getFamilies().subscribe(res => {
      this.families = res;
    }, err =>{
      // FIXME : ouvrir pop-up erreur
    });
    this.projectLibraryService.getSystems().subscribe(res => {
      this.systems = res;
    }, err => {
      // FIXME : ouvrir pop-up erreur
    });
    this.route.queryParams.subscribe(params => {
      if(params['id'] !== undefined) {
        this.isNew = false;
        this.idVersion = params['id'];
        this.initComponent();
      }else {
        this.isNew = true;
      }
    });
  }

  initComponent() {
    this.projectLibraryService.getVersion(this.idVersion).subscribe(res => {
      this.version = res;
    }, err => {
      // FIXME : ouvrir pop-up erreur
    });
  }

  refreshSystem(event): void {
    if( event !== 'null') {
      this.projectLibraryService.getFamilyById(event).subscribe(res => {
        this.version.service.theme = res;
      }, err => {
        // FIXME : ouvrir pop-up erreur
      });

      this.projectLibraryService.getSystemByFamilyId(event).subscribe(res => {
        this.systems = res;
      }, err => {
        // FIXME : ouvrir pop-up erreur
      });

    }else {
      this.projectLibraryService.getSystems().subscribe(res => {
        this.systems = res;
      },  err => {
        // FIXME : ouvrir pop-up erreur
      });
      this.version.service.id = null;
    }
  }


  onChangeSystem(event) {
    if(event !== 'null') {
      this.projectLibraryService.getSystemById(event).subscribe(res => {
        this.version.service = res;
      }, err => {
        // FIXME : ouvrir pop-up erreur
      });
    }else {
      this.version.service.id = null;
    }
  }



  saveVersion(): void {
    if(!this.versionForm.invalid) {
      if(this.idVersion === undefined) {
        this.projectLibraryService.createVersion(this.version).subscribe(res => {
          this.router.navigate(['/pages/versions/listVersions']);
        }, err => {
          // FIXME : ouvrir pop-up erreur
        });
      }else {
        this.projectLibraryService.updateVersion(this.version).subscribe(res => {
          this.router.navigate(['/pages/versions/listVersions']);
        });
      }
    }
  }

  back() {
    this.router.navigate(['/pages/versions/listVersions']);
  }
}
