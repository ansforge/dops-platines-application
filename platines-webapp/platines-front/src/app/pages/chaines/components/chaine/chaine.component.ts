///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {Component, OnInit, ViewChild} from '@angular/core';
import {ChainesService} from '../../../../services/chaines.service';
import {NotifParams} from '../../../entity/notifparams';
import {ActivatedRoute, Router} from '@angular/router';
import {AbstractControl, UntypedFormBuilder, UntypedFormGroup, Validators} from '@angular/forms';
import {ChainOfTrustList} from '../../../entity/chainoftrustlist';
import {ChainOfTrust} from '../../../entity/chainoftrust';
import {User} from '../../../entity/user';
import {InfoModalComponent} from "../../../ui/components/info-modal/info-modal.component";
import {ConfirmModalComponent} from '../../../ui/components/confirmModal/confirmModal.component';
import {ProfileService} from '../../../../services/profile.service';
import {FilterListService} from '../../../../services/filterlist.service';
import {MatTableDataSource} from "@angular/material/table";
import {SelectionModel} from "@angular/cdk/collections";
import { PlatineModalService } from '../../../ui/components/modal/platine-modal.service';
import { CURRENT_USER_KEY } from '../../../../services/authentication.service';

@Component({
  selector: 'chaine',
  templateUrl: './chaine.html',
  styleUrls: ['./chaine.scss'],
})
export class Chaine implements OnInit {

  autoColumns: string[] = [];
  allColumns: string[] = [...this.autoColumns, 'validityDate', 'pem', 'actions', 'checkbox'];
  dataSource: MatTableDataSource<any> = new MatTableDataSource();
  selection: SelectionModel<any> = new SelectionModel<any>(true, []);
  notifParams: NotifParams = new NotifParams();
  chainOfTrust: ChainOfTrust = new ChainOfTrust();
  chainNameSnapshot: string;
  chaines: ChainOfTrustList[];
  form: UntypedFormGroup;
  nom: AbstractControl;
  description: AbstractControl;
  user = JSON.parse(localStorage.getItem(CURRENT_USER_KEY)).user;//TODO review this to use the Profile object from AuthenticationService
  isNew: boolean = true;
  id: number;
  certificates;

  constructor(public fb: UntypedFormBuilder, private route: ActivatedRoute, private router: Router,
    private _serviceChaine: ChainesService/*, private _service: NotificationsService*/, private modalService: PlatineModalService, private profileService: ProfileService
  ,private filterListService: FilterListService) {
    this.form = this.fb.group({
      nom: ['', Validators.compose([Validators.required, Validators.minLength(4)])],
      description: ['', Validators.compose([Validators.required, Validators.minLength(4)])],
    });
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
  }

  initComponent() {
    this._serviceChaine.getChaine(this.id).subscribe(result => {
      this.chainOfTrust = result;
      this.chainNameSnapshot = result.name;
      this.dataSource.data = result.certificate;
    }, err => {
      // this._service.error("Erreur", "Erreur lors de la récupération de la chaine de confiance", this.notifParams.notif)
    });
  }

  back() {
    this.router.navigate(['/pages/chaines/list']);
  }

  addCertificate() {
    this.router.navigate(['/pages/chaines/certificat'], { queryParams: { new: true, idCh: this.id } });
  }

  upadateCertificate(idCertificat: number) {
    this.router.navigate(['/pages/chaines/certificat'],
      { queryParams: { new: false, idCh: this.id, idCer: idCertificat } });
  }

  deleteCertificate(event): void {
    if (this.selection.selected.length !== 0) {

      let supressCertificats = this.filterListService.filterListForDelete(this.selection.selected, this.dataSource.data);

      this.modalService.openConfirm({
        modalHeader : 'Suppression de certificats',
        modalContent : `Êtes-vous sûr de vouloir supprimer les certificats sélectionnés (${supressCertificats.length}) ?`
      }).then((res) => {
        if (res === 'confirm') {

          for (let i = supressCertificats.length - 1; i >= 0; i--) {
            const obj = supressCertificats[i];
            const obj2 = JSON.parse(JSON.stringify(supressCertificats[i]));
            this._serviceChaine.deleteCertificate(obj2.id).subscribe(result => {
              this.dataSource.data = this.dataSource.data.filter(item => item.id !== obj.id);
              this.selection.clear();
            }, err => {
              // this._service.error('Erreur :', 'Erreur de suppression', this.notifParams.notif);
            });
          }
        }
      });
    }
  }

  addChain() {
    if (this.id === null) {
      this.chainOfTrust.user = new User();
      this.chainOfTrust.user.mail = this.profileService.getJwtToken().sub;
      this._serviceChaine.addChain(this.chainOfTrust).subscribe(result => {
        this.router.navigate(['/pages/chaines/chaine'],
          { queryParams: { new: false, id: result } });

        this.modalService.openInfoModal(
          {
            modalHeader: 'Ajout de chaine',
            modalContent: 'Chaine ajoutée avec succès'
          }
        );
        // this._service.success('', 'Chaine ajoutée avec succès', this.notifParams.notif);
      }, err => {
        // this._service.error('', `Erreur lors d'ajout de la chaine`, this.notifParams.notif);
      });
    } else {
      this.chainOfTrust.id = this.id;
      this._serviceChaine.updateChain(this.chainOfTrust).subscribe(result => {
        this.modalService.openInfoModal({
          modalHeader : 'Modification de chaine',
          modalContent : 'Modification effectuée avec succès'
        });
        // this._service.success('', 'Modification effectuée avec succès', this.notifParams.notif);
      }, err => {
        // this._service.error('', 'Erreur lors des modifications de la chaine', this.notifParams.notif);
      });
    }

  }

  setAll(element) {
    if (this.isAllSelected() || this.selection.hasValue()) {
      this.selection.clear();
      element.target.checked = false;
    } else {
      this.dataSource.data.forEach(row => this.selection.select(row));
    }
  }

  setRow(element) {
    this.selection.toggle(element);
  }

  isAllSelected() {
    const numSelected = this.selection.selected.length;
    const numRows = this.dataSource.data.length;
    return numSelected === numRows;
  }

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
  }

}
