///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {ChangeDetectorRef, Component, OnInit, reflectComponentType, ViewEncapsulation} from '@angular/core';
import {UntypedFormBuilder, UntypedFormGroup} from '@angular/forms';
import {ActivatedRoute, Router} from '@angular/router';
import {ProjectLibraryService} from '../../services/projectlibrary.service';
import {ProjectList} from '../entity/projectlist';
import {Version} from '../entity/version';
import {System} from '../entity/system';
import {Theme} from '../entity/theme';
import {NotifParams} from '../entity/notifparams';
import {ContextStateService} from "../../services/context-state.service";
import { CURRENT_USER_KEY } from '../../services/authentication.service';

@Component({
  selector: 'compatibilite',
  templateUrl: './compatibilite.html',
  styleUrls: ['./compatibilite.scss']
})
export class CompatibiliteComponent implements OnInit {
  userStock = JSON.parse(localStorage.getItem(CURRENT_USER_KEY));//TODO review this to use the Profile object from AuthenticationService
  familles: Theme[];
  systemes: System[];
  form: UntypedFormGroup;
  versionsSelectedSysteme: Version[];
  projets: ProjectList[];
  allProjets: ProjectList[];
  role: string;
  notifParams: NotifParams = new NotifParams();

  constructor(public fb: UntypedFormBuilder,
              private projectService: ProjectLibraryService,
              private router: Router,
              private route: ActivatedRoute,
              private contextStateService: ContextStateService,
              private cdr: ChangeDetectorRef) {
    this.form = this.fb.group({
      familleId: '',
      systemeId: '',
    });
  }

  ngOnInit() {
    this.role = this.contextStateService.compatibilityRole;
    this.projectService.getFamilies().subscribe(data => {
      this.familles = data;
    });
    this.projectService.getAllProjects().subscribe(data => {
      if (data !== null) {
        this.allProjets = data;
        this.projets = [];
        this.allProjets.forEach(element => {
          if (element.role === this.role) {
            this.projets.push(element);
          }
        });
        setTimeout(() => {
          window.scrollTo(0, this.contextStateService.scrollPositions.get(reflectComponentType(CompatibiliteComponent).selector));
        });
      }
    });
    this.form.patchValue({
      familleId: this.contextStateService.compatibilityFamily
    });
    if (this.contextStateService.compatibilityFamily !== null)
      this.refreshSysteme();
    this.form.patchValue({
      systemeId: this.contextStateService.compatibilitySystem
    });
    this.getVersions();
    this.systemDropdownChangeDeactivated();
  }

  ngAfterContentChecked() {
    this.cdr.detectChanges();
  }

  systemDropdownChangeDeactivated() {
    if (this.systemes.length < 1) {
      this.form.controls['systemeId'].disable();
    } else {
      this.form.controls['systemeId'].enable();
    }
  }


  ngOnDestroy() {
    this.contextStateService.compatibilityFamily = this.form.value.familleId;
    this.contextStateService.compatibilitySystem = this.form.value.systemeId;
    this.contextStateService.compatibilityRole = this.role;
    this.contextStateService.scrollPositions.set(reflectComponentType(CompatibiliteComponent).selector, window.scrollY);
  }

  boolean(idProjet, idVersion): Boolean {
    let projetATester: ProjectList;
    let ok: Boolean = false;

    for (let x = 0; x < this.projets.length; x++) {
      let projet: ProjectList;
      projet = this.projets[x];
      if (projet.id === idProjet) {
        projetATester = projet;
      }
    }

    for (let x = 0; x < projetATester.versions.length; x++) {
      let version: Version;
      version = projetATester.versions[x];
      if (version.id === idVersion) {
        ok = true;
      }
    }
    return ok;

  }

  onRadioChange($event) {
    this.role = $event.value;
    this.projets = [];
    this.allProjets.forEach(element => {
      if (element.role === this.role) {
        this.projets.push(element);
      }
    });
  }

  majAssociation(idVersion, idProjet) {
    const formData = new FormData();
    formData.append('idProject', idProjet);
    formData.append('idVersion', idVersion);
    this.projectService.mapping(formData)
      .subscribe(res => {
        this.projectService.getAllProjects().subscribe(data => {
          //this._service.success('Modification', 'Succès');
          this.allProjets = data;
          this.projets = [];
          this.allProjets.forEach(element => {
            if (element.role === this.role) {
              this.projets.push(element);
            }
          });
        });

      }, err => {
        //this._service.error("Modification", "Echec", this.notifParams.notif);
      });

  }

  refreshSysteme(): void {
    this.systemes = [];
    this.form.patchValue({systemeId: ""});
    this.versionsSelectedSysteme = undefined;
    if (this.form.value.familleId !== undefined && this.form.value.familleId !== '') {
      this.projectService.getSystemByFamilyId(this.form.value.familleId).subscribe(data => {
        this.systemes = data;
        this.systemDropdownChangeDeactivated();
      });
    } else {
      this.systemDropdownChangeDeactivated();
    }
  }

  getVersions(): void {
    if (this.form?.value?.systemeId === '') {
      this.versionsSelectedSysteme = undefined;
    } else if (this.form?.value?.systemeId !== undefined) {
      this.projectService.getVersionBySystem(this.form.value.systemeId).subscribe(res => {
        this.versionsSelectedSysteme = res;
      })
    }
  }
}
