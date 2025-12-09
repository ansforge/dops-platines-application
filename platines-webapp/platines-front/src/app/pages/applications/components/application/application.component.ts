///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {ChangeDetectorRef, Component, OnInit, ViewChild} from '@angular/core';
import {ApplicationsService} from '../../../../services/application.service';
import {ActivatedRoute, Router} from '@angular/router';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {ChainesService} from '../../../../services/chaines.service';
import {UsersService} from '../../../../services/users.service';
import {ProfileService} from '../../../../services/profile.service';
import {ChainOfTrustList} from '../../../entity/chainoftrustlist';
import {Application} from '../../../entity/application';
import {NotifParams} from '../../../entity/notifparams';
import {PlatineModalService} from "../../../ui/components/modal/platine-modal.service";
import {TranslateService} from "@ngx-translate/core";
import { CURRENT_USER_KEY } from '../../../../services/authentication.service';

@Component({
  selector: 'application',
  templateUrl: './application.html',
  styleUrls: ['./application.scss'],
})
export class ApplicationComponent implements OnInit {

  @ViewChild('applicationForm')
  applicationForm;
  chaineDeConfiance: ChainOfTrustList[] = new Array<ChainOfTrustList>();
  app: Application = new Application();
  user = JSON.parse(localStorage.getItem(CURRENT_USER_KEY));//TODO review this to use the Profile object from AuthenticationService
  id;
  notifParams: NotifParams = new NotifParams();
  newApp = "";
  appNameSnapshot: string;
  isNew: boolean;
  isTlsRequired: boolean = false;
  protected missingChainOfTrust: boolean = false;

  /**
   * Constructeur du composant ApplicationComponent
   * @param applicationService
   * @param route
   * @param router
   * @param modalService
   * @param cdr
   * @param chainService
   * @param userService
   * @param profileService
   * @param platinesModalService
   * @param translateService
   */
  constructor(private cdr: ChangeDetectorRef,
              private applicationService: ApplicationsService,
              private route: ActivatedRoute,
              private router: Router,
              private modalService: NgbModal,
              private chainService: ChainesService,
              private userService: UsersService,
              private profileService: ProfileService,
              private platinesModalService: PlatineModalService,
              private translateService: TranslateService) {
  }

  /**
   * Récupération des paramètres pour accéder à la page web.
   * Si l'id est renseigné on charge les infos de l'application concernée
   * sinon tout les champs sont vides.
   */
  ngOnInit() {
    this.route.queryParams.subscribe(params => {
        if (params['id'] !== undefined && params['new'] === undefined) {
          this.isNew = false;
          this.id = params['id'];
          this.loadApplication(this.id);
        } else {
          this.isTlsRequired = false;
          this.isNew = true;
          this.app.chainOfTrustDto = null;
          this.app.role = 'SERVER';
          this.userService.getUserByMail(this.profileService.getJwtToken().sub).subscribe(user => {
            this.chainService.getChainByUser(user.id).subscribe({
              next: res => this.chaineDeConfiance = res,
              error: () => this.openChainOfTrustLoadingErrorModal()
            });
          })
        }
      }
    );
  }

  ngAfterContentChecked() {
    this.cdr.detectChanges();
  }

  /**
   * Chargement de l'application à l'aide de son id.
   * @param id
   */
  loadApplication(id) {
    this.applicationService.getApplicationById(id).subscribe(data => {
      this.app = data;
      this.appNameSnapshot = this.app.name;
      this.isTlsRequired = this.app.chainOfTrustDto !== null;
      this.userService.getUserByMail(this.profileService.getJwtToken().sub).subscribe(user => {
        if (this.app.user.mail === user.mail) {
          this.chainService.getAllChaines().subscribe({
            next: res => this.chaineDeConfiance = res,
            error: () => this.openChainOfTrustLoadingErrorModal()
          });
        } else {
          this.chainService.getChainByUser(this.app.user.id).subscribe({
            next: res => this.chaineDeConfiance = res,
            error: () => this.openChainOfTrustLoadingErrorModal()
          });
        }
      })
    });
  }

  setChain(idChain) {
    if (idChain != "null") {
      this.missingChainOfTrust = false;
      this.app.chainOfTrustDto = new ChainOfTrustList();
      this.app.chainOfTrustDto.id = idChain;
    } else {
      this.missingChainOfTrust = true;
      this.app.chainOfTrustDto = null;
    }
  }

  /**
   * Méthode d'ajout de l'application à la base de données.
   * Si l'application possède un id on fait un update de l'application avec les champs
   * renseignés, sinon on effectue une nouvelle entrée dans la base de données.
   */
  createApplication(): void {
    if (this.applicationForm.valid) {
      if (this.id === undefined) {
        this.app.user.mail = this.profileService.getJwtToken().sub;
        this.applicationService.createApplication(this.app).subscribe(() => {
          void this.router.navigate(['/pages/applications/listApplications']);
        });
      } else {
        this.applicationService.updateApplication(this.app).subscribe(() => {
          void this.router.navigate(['/pages/applications/listApplications']);
        });
      }
    }
  }

  back() {
    void this.router.navigate(['/pages/applications/listApplications']);
  }

  openChainOfTrustLoadingErrorModal() {
    this.platinesModalService.openErrorModalWithCustomMessage({
      modalHeader: this.translateService.instant('pages.chains.errors.loading_title'),
      modalContent: this.translateService.instant('pages.chains.errors.loading_msg'),
    });
  }

  toggleTlsRequirement() {
    this.app.chainOfTrustDto = null;
    this.missingChainOfTrust = this.isTlsRequired;
  }
}
