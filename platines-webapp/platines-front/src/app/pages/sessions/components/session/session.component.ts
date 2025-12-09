///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {Component, OnInit, ViewChild} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {SessionService} from '../../../../services/session.service';
import {UsersService} from '../../../../services/users.service';
import {ProfileService} from '../../../../services/profile.service';
import {ProjectLibraryService} from '../../../../services/projectlibrary.service';
import {ApplicationsService} from '../../../../services/application.service';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {ProjectsModalComponent} from '../../../ui/components/projectsModal/projectsModal.component';
import {Session} from '../../../entity/session';
import {ProjectResult} from '../../../entity/project.result';
import {Application} from '../../../entity/application';
import {Version} from '../../../entity/version';
import {SessionDuration} from '../../../entity/session.duration';
import {NotifParams} from '../../../entity/notifparams';
import {User} from '../../../entity/user';
import {Theme} from '../../../entity/theme';
import {System} from '../../../entity/system';
import {ProjectSession} from '../../../entity/projectsession';
import {Property} from '../../../entity/property';
import {CdkDragDrop, moveItemInArray} from "@angular/cdk/drag-drop";
import {PlatineModalService} from "../../../ui/components/modal/platine-modal.service";
import {coversAppRole, FamiliesRoles} from '../../../entity/familyRoleCoverage';
import {toTestWebserviceRole} from '../../../entity/role';
import {TranslateService} from "@ngx-translate/core";

@Component({
  selector: 'sessions',
  templateUrl: './session.html',
  styleUrls: ['./session.scss'],
})
export class SessionComponent implements OnInit {


  @ViewChild('sessionForm')
  sessionForm;
  @ViewChild('projectsForm')
  projectsForm;
  notifParams: NotifParams = new NotifParams();
  user: User = new User();
  idSession: number;
  session: Session = new Session();
  users: User[] = new Array<User>();
  applications: Application[] = new Array<Application>();
  selectedUserFamilies: Theme[] = new Array<Theme>();
  userFamilies: FamiliesRoles[] = new Array<FamiliesRoles>();
  families: Theme[] = new Array<Theme>();
  systems: System[] = new Array<System>();
  versions: Version[] = new Array<Version>();
  //liste des Rprojets
  projects: ProjectResult[] = new Array<ProjectResult>();
  //liste des projets dasn la modal
  projectsList: ProjectSession[] = new Array<ProjectSession>();
  durations: SessionDuration[] = new Array<SessionDuration>();
  //projet selectionné dans la modal
  projectM: ProjectSession = new ProjectSession();
  projectResult: ProjectResult = new ProjectResult();
  properties: Property[] = new Array<Property>();
  isAdmin = false;
  isManager = false;
  isDuplicate = false;
  duplicateSession: any;
  new: any;
  loadingProjects: boolean;

  constructor(
    private sessionService: SessionService,
    private userService: UsersService,
    private projectLibraryService: ProjectLibraryService,
    private applicationService: ApplicationsService,
    private route: ActivatedRoute,
    private router: Router,
    private modalService: NgbModal,
    private profileService: ProfileService,
    private platinesModalService: PlatineModalService,
    private translateService: TranslateService) {
    this.session.application = new Application();
    this.session.version = new Version();
    this.session.sessionDuration = new SessionDuration();
  }

  ngOnInit() {
    this.projectLibraryService.getFamiliesRoleCoverage().subscribe({
      next:res => {
        this.userFamilies = res;
      },
      error:() => {
        this.platinesModalService.openErrorModalWithCustomMessage({
          modalHeader: this.translateService.instant('pages.sessions.session.errors.user_families_coverage_title'),
          modalContent: this.translateService.instant('pages.sessions.session.errors.user_families_coverage_msg')
        })
      }
    });

    this.route.queryParams.subscribe(params => {
      if (params['idSession'] !== undefined) {
        this.idSession = params['idSession'];
        this.isDuplicate = params['isDuplicate'];
        this.sessionService.getSessionById(this.idSession).subscribe({
          next:session => {
            this.session = session;
            this.projectLibraryService.getFamiliesRoleCoverage().subscribe(families => {
              this.userFamilies = families;
              if (session.application.user.profile.label == "admin") {
                this.families = families.filter(cov => coversAppRole(cov,session.application.role)).flatMap(cov => cov.family);
              } else {
                this.selectedUserFamilies = session.application.user.families;
                this.selectedUserFamilies.forEach(
                  value => {
                    const appRoleFamilies: Theme[] = this.userFamilies.filter(cov => coversAppRole(cov,session.application.role)).flatMap(cov => cov.family);

                    this.families = this.families.concat(
                      appRoleFamilies.filter(f => f.id == value.id))
                  });
              }
            });
            this.initComponent();
            this.refreshSystem(this.session.version.service.theme.id);
            this.refreshVersion(this.session.version.service.id);
            this.refreshProjects(this.session.version.id);
          },
          error:() => {
            void this.router.navigate(['/sessions']);
          }
        })
      } else {
        this.userService.getUserByMail(this.profileService.getJwtToken().sub).subscribe(user => {
          this.user = user;
          this.session.application.user.id = user.id;
          this.initComponent();
        });
      }
    });
    if (this.profileService.getProfileFromToken() === "admin") {
      this.isAdmin = true;
      this.userService.getAllUsers().subscribe({
        next:res => {
          this.users = res;
        },
        error:() => {
          this.platinesModalService.openErrorModalWithCustomMessage({
            modalHeader: this.translateService.instant('pages.sessions.session.errors.all_users_title'),
            modalContent: this.translateService.instant('pages.sessions.session.errors.all_users_msg')
          })
        }
      });
    } else if (this.profileService.getProfileFromToken() === "manager") {
      this.isManager = true;
      this.userService.getAllUsers().subscribe({
        next:res=> {
          this.users = res;
        }, error:() => {
          this.platinesModalService.openErrorModalWithCustomMessage({
            modalHeader: this.translateService.instant('pages.sessions.session.errors.all_users_title'),
            modalContent: this.translateService.instant('pages.sessions.session.errors.all_users_msg')
          })
        }
      });
    }
  }

  initComponent() {
    this.sessionService.getUserApplications(this.session.application.user.id).subscribe({
      next: res => {
        this.applications = res;
      },
      error: () => {
        this.platinesModalService.openErrorModalWithCustomMessage({
          modalHeader: this.translateService.instant('pages.sessions.session.errors.applications_title'),
          modalContent: this.translateService.instant('pages.sessions.session.errors.applications_msg')
        })
      }
    });
    this.sessionService.getSessionDurations().subscribe({
      next: res => {
        this.durations = res;
      },
      error: () => {
        this.platinesModalService.openErrorModalWithCustomMessage({
          modalHeader: this.translateService.instant('pages.sessions.session.errors.durations_title'),
          modalContent: this.translateService.instant('pages.sessions.session.errors.durations_msg')
        })
      }
    });
  }

  selectApplication() {
    this.deselectFamily();
    this.deselectSystem();
    this.deselectVersion();
    this.deselectProjects();
    this.refreshFamilies();
  }

  selectFamily(idFamily: Number) {
    this.deselectSystem();
    this.deselectVersion();
    this.deselectProjects();
    this.refreshSystem(idFamily);
  }

  selectSystem(idSystem: Number) {
    this.deselectVersion();
    this.deselectProjects();
    this.refreshVersion(idSystem);
  }

  selectVersion(idVersion: Number) {
    this.deselectProjects();
    this.refreshProjects(idVersion);
  }

  refreshFamilies() {
    this.systems = [];
    this.versions = [];
  }

  refreshSystem(idFamily: Number) {
    this.systems = [];
    this.versions = [];
    if (idFamily.toString() != "null") {
      const simulatedRole = toTestWebserviceRole(this.session.application.role);
      this.projectLibraryService.getSystemByFamilyIdWithRole(idFamily,simulatedRole).subscribe({
        next:res => {
          this.systems = res;
        }, error:() => {
          this.platinesModalService.openErrorModalWithCustomMessage({
            modalHeader: this.translateService.instant('pages.sessions.session.errors.systems_title'),
            modalContent: this.translateService.instant('pages.sessions.session.errors.systems_msg')
          })
        }
      });
    }
  }

  refreshVersion(idSystem: Number) {
    this.versions = [];
    if (idSystem.toString() != "null"){
      const simulatedRole = toTestWebserviceRole(this.session.application.role);
      this.projectLibraryService.getVersionBySystemWithRole(idSystem,simulatedRole).subscribe({
        next:res => {
          this.versions = res;
        }, error:() => {
          this.platinesModalService.openErrorModalWithCustomMessage({
            modalHeader: this.translateService.instant('pages.sessions.session.errors.versions_title'),
            modalContent: this.translateService.instant('pages.sessions.session.errors.versions_msg')
          })
        }
      });
    }
  }

  refreshProjects(idVersion: Number) {
    this.loadingProjects = true;
    this.projectsList = [];
    if (idVersion.toString() != "null"){
      this.sessionService.getProjects(idVersion, this.session.application.role).subscribe({
        next:res => {
          this.projectsList = res;
          this.loadingProjects = false;
        }, error:() => {
          this.loadingProjects = false;
          this.platinesModalService.openErrorModalWithCustomMessage({
            modalHeader: this.translateService.instant('pages.sessions.session.errors.projects_title'),
            modalContent: this.translateService.instant('pages.sessions.session.errors.projects_msg')
          });
        }
      });
    }
  }

  changeVersion(idVersion: Number) {
    this.loadingProjects = true;
    this.session.projectResults = [];
    if (idVersion.toString() != "null"){
      this.sessionService.getProjects(idVersion, this.session.application.role).subscribe({
        next:res=> {
          this.projectsList = res;
          this.loadingProjects = false;
        }, error:() => {
          this.platinesModalService.openInfoModal({
            modalHeader: "Erreur",
            modalContent: "Une erreur est survenue lors de la récupération des projets",
          })
          this.loadingProjects = false;
        }
      });
    }
  }

  changeUser(idUser: Number) {
    //charge la liste des applications de l'user
    this.applications = [];
    this.session.version = new Version();
    this.sessionService.getUserApplications(idUser).subscribe({
      next:res=> {
        this.applications = res;
      }, error:() => {
        this.platinesModalService.openErrorModalWithCustomMessage({
          modalHeader: this.translateService.instant('pages.sessions.session.errors.applications_title'),
          modalContent: this.translateService.instant('pages.sessions.session.errors.applications_msg')
        })
      }
    });
  }

  changeApplication(idApplication: Number) {
    this.applicationService.getApplicationById(idApplication).subscribe({
      next:app => {
        let role = this.session.application.role;
        this.session.application = app;
        if (this.session.application.role === "CLIENT") {
          this.session.sessionDuration = new SessionDuration();
        }
        this.families = [];
        if (app.user.profile.label == "admin") {
          this.families = this.userFamilies.filter(cov => coversAppRole(cov,app.role)).flatMap(cov => cov.family);
        } else {
          this.selectedUserFamilies = app.user.families;
          this.selectedUserFamilies.forEach(
            value => this.families = this.families.concat(
              this.userFamilies.filter(cov => coversAppRole(cov,app.role)).filter(f => f.family.id == value.id).flatMap(cov => cov.family))
          );
        }

        if (role !== undefined && role !== this.session.application.role) {
          this.session.projectResults = [];
        }
      },
      error:() => {
        this.platinesModalService.openErrorModalWithCustomMessage({
          modalHeader: this.translateService.instant('pages.sessions.session.errors.application_title'),
          modalContent: this.translateService.instant('pages.sessions.session.errors.application_msg')
        })
      }
    });
    this.selectApplication();
  }

  openModal() {
    if ((this.session.projectResults.length == 0 && this.session.application.role === 'CLIENT') || this.session.application.role === 'SERVER') {

      const activeModal = this.modalService.open(ProjectsModalComponent, {size: 'lg'});
      activeModal.componentInstance.url = this.session.application.url;
      activeModal.componentInstance.projectsList = this.projectsList;
      activeModal.result.then((res) => {
        if (res !== undefined) {
          let projectResult: ProjectResult = new ProjectResult();
          projectResult.name = res.project.name;
          projectResult.idProject = res.project.id;
          projectResult.projectProperties = res.properties;
          this.session.projectResults.push(projectResult);

        }
      })
    } else {
      this.platinesModalService.openInfoModal({
        modalHeader: "Erreur",
        modalContent: "Vous ne pouvez pas ajouter plus de tests à cette session."
      })
    }
  }

  saveSession(): void {
    if (!this.sessionForm.invalid && this.session.projectResults.length !== 0) {
      for (const rProject of this.session.projectResults) {
        rProject.numberOrder = this.session.projectResults.indexOf(rProject) + 1;
      }

      if (this.session.application.role === 'SERVER') {
        this.session.sessionDuration = null;
      }
      this.session.sessionType = 'RECIPE';
      if (this.idSession !== undefined) {
        if (this.isDuplicate) {
          this.session.id = undefined;
          this.session.projectResults.forEach(p => p.id = null);
          this.sessionService.duplicateSession(this.session).subscribe(() => {
            void this.router.navigate(['/pages/sessions/listSessions']);
          });
        } else {
          this.sessionService.updateSession(this.session).subscribe({
            next:() => {
              void this.router.navigate(['/pages/sessions/listSessions']);
            },
            error:() => {
              this.platinesModalService.openErrorModalWithCustomMessage({
                modalHeader: this.translateService.instant('pages.sessions.session.errors.update_title'),
                modalContent: this.translateService.instant('pages.sessions.session.errors.update_msg')
              })
            }
          });
        }
      } else {
        this.sessionService.createSession(this.session).subscribe({
          next:() => {
            void this.router.navigate(['/pages/sessions/listSessions']);
          },
          error:() => {
            this.platinesModalService.openErrorModalWithCustomMessage({
              modalHeader: this.translateService.instant('pages.sessions.session.errors.create_title'),
              modalContent: this.translateService.instant('pages.sessions.session.errors.create_msg')
            })
          }
        });
      }
    }
  }

  back() {
    this.router.navigate(['/pages/sessions/listSessions']);
  }


  openEditModal(projectRes) {
    const activeModal = this.modalService.open(ProjectsModalComponent, {size: 'lg'});
    activeModal.componentInstance.url = this.session.application.url;
    activeModal.componentInstance.projectsList = this.projectsList;
    activeModal.componentInstance.projectM = projectRes;
  }

  deleteProject(rProject) {
    const index = this.session.projectResults.indexOf(rProject);
    this.session.projectResults.splice(index, 1);
    this.sessionService.getProjects(this.session.version.id, this.session.application.role).subscribe(res => {
      this.projectsList = res;
    });
  }

  drop(event: CdkDragDrop<string[]>) {
    moveItemInArray(this.session.projectResults, event.previousIndex, event.currentIndex);
  }

  private deselectFamily() {
    this.session.version.service.theme.id = null;
  }

  private deselectSystem() {
    this.session['version']['service']['id'] = null;
  }

  private deselectVersion() {
    this.session['version']['id'] = null;
  }

  private deselectProjects() {
    this.session.projectResults = [];
  }
}
