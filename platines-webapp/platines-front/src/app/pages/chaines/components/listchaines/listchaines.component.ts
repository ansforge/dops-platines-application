///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {Component, reflectComponentType, ViewChild} from '@angular/core';
import {ChainesService} from '../../../../services/chaines.service';
import {NotifParams} from '../../../entity/notifparams';
import {Router} from '@angular/router';
import {ChainOfTrustList} from '../../../entity/chainoftrustlist';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {ConfirmModalComponent} from '../../../ui/components/confirmModal/confirmModal.component';
import jwt_decode from "jwt-decode";
import {FilterListService} from '../../../../services/filterlist.service';
import {MatTableDataSource} from "@angular/material/table";
import {SelectionModel} from "@angular/cdk/collections";
import {MatSort, Sort} from "@angular/material/sort";
import {MatPaginator} from "@angular/material/paginator";
import {ContextStateService} from "../../../../services/context-state.service";
import { CURRENT_USER_KEY } from '../../../../services/authentication.service';
import { PlatineModalService } from "../../../ui/components/modal/platine-modal.service";

export interface ChainOfTrust {
  id: number;
  description: string;
  name: string;
  user: any;
  valide: boolean;

}

@Component({
  selector: 'listchaines',
  templateUrl: './listchaines.html',
  styleUrls: ['./listchaines.scss']
})
export class Listchaines {

  @ViewChild(MatPaginator) paginator: MatPaginator;
  @ViewChild(MatSort) sort: MatSort;

  autoColumns: string[] = ['name', 'description'];
  allColumns: string[] = ['valide', ...this.autoColumns, 'user', 'actions', 'checkbox'];
  dataSource: MatTableDataSource<any> = new MatTableDataSource();
  selection: SelectionModel<any> = new SelectionModel<any>(true, []);
  userStock = jwt_decode(localStorage.getItem(CURRENT_USER_KEY));//TODO review this to use the Profile object from AuthenticationService
  notifParams: NotifParams = new NotifParams();
  chaines: ChainOfTrustList[];
  labelsPath: string = "pages.chains.labels";
  metadata = reflectComponentType(Listchaines);

  constructor(private router: Router,
              private _serviceChaine: ChainesService,
              private modalService: PlatineModalService,
              private filterListService: FilterListService,
              protected contextStateService: ContextStateService) {
    let isAdmin = true;
    if (this.userStock["auth"][0].authority === "user") {
      isAdmin = false;
    }
    this.loadChain();

  }

  ngOnInit() {
    this.dataSource.filterPredicate = function (data: any, filter: string): boolean {
      return data.name?.toLowerCase().includes(filter) ||
        data.description?.toLowerCase().includes(filter) ||
        (data.user?.name + " " + data.user?.forename).toLowerCase().includes(filter);
    }
  }

  ngOnDestroy() {
    this.contextStateService.persistContext(
      this.paginator.pageIndex,
      this.paginator.pageSize,
      this.sort.active,
      this.sort.direction,
      window.scrollY,
      this.metadata.selector);
  }

  ngAfterViewInit() {
  }

  loadChain() {
    this._serviceChaine.getAllChaines().subscribe(res => {
      this.dataSource.data = res;
      this.contextStateService.restoreContext(
        this.dataSource,
        this.sort,
        this.paginator,
        this.metadata.selector
      )
      setTimeout(() => this.contextStateService.refreshPaginator(this.dataSource));
    }, err => {
      //this._service.error("Erreur de chargement", "Erreur lors du chargement des chaines de confiance", this.notifParams.notif)
    });
  }

  onUserRowSelect(chainId): void {
    this.router.navigate(['/pages/chaines/chaine'], {queryParams: {id: chainId}});
  }

  deleteChain() {

    if (this.selection.selected.length > 0) {
      let supressChaines = this.filterListService.filterListForDelete(this.selection.selected, this.dataSource.data);
      const activeModal = this.modalService.openConfirm({
        modalHeader:'Suppression d\'une chaine de confiance',
        modalContent: `Êtes-vous sûr de vouloir supprimer les chaines de confiance sélectionnées (${supressChaines.length}) ?`
      });
        
      activeModal.then((res) => {
        if (res === 'confirm') {

          for (let i = supressChaines.length - 1; i >= 0; i--) {
            const obj = supressChaines[i];
            const obj2 = JSON.parse(JSON.stringify(supressChaines[i]));
            this._serviceChaine.deleteChain(obj2.id).subscribe(result => {
              this.dataSource.data = this.dataSource.data.filter(c => c.id !== obj.id);
              setTimeout(() => this.contextStateService.refreshPaginator(this.dataSource));
              this.selection.clear();
            }, err => {
              this.modalService.openErrorModal({
                modalHeader: "Erreur lors de la suppression d'une chaîne de confiance",
                err: err
              });
            });
          }
        }
      });
    }
  }

  createChain() {
    this.router.navigate(['/pages/chaines/chaine'], {queryParams: {new: true}});
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

  applyFilter() {
    this.dataSource.filter = this.contextStateService.filters.get(this.metadata.selector);
  }

  sortData(sort: Sort) {
    const sortedData = this.dataSource.data.slice();
    if (sort.active === 'user')
      sortedData.sort((a, b) => this.sortAlphanumeric(a?.user?.forename + " " + a?.user?.name, b?.user?.forename + " " + b?.user?.name, sort.direction === 'asc'));
    else
      sortedData.sort(
        (a, b) => sort.direction === 'asc' ?
          this.sortAlphanumeric(a[sort.active], b[sort.active], true) :
          this.sortAlphanumeric(a[sort.active], b[sort.active], false));
    this.dataSource.data = sortedData;
  }

  sortAlphanumeric(a, b, isAsc: boolean): number {
    if (typeof a === 'boolean' && typeof b === 'boolean') {
      if (isAsc) {
        if (a === b) {
          return 0;
        } else {
          if (a) return 1;
          else return -1;
        }
      } else {
        if (a === b) {
          return 0;
        } else {
          if (a) return -1;
          else return 1;
        }
      }
    } else {
      if (isAsc) {
        if (!a || !b) {
          return !a ? -1 : 1;
        } else {
          return a.localeCompare(b, 'en', {numeric: true});
        }
      } else {
        if (!a || !b) {
          return !a ? 1 : -1;
        } else {
          return b.localeCompare(a, 'en', {numeric: true});
        }
      }
    }
  }

  isAllSelected() {
    const numSelected = this.selection.selected.length;
    const numRows = this.dataSource.data.length;
    return numSelected === numRows;
  }

  editChain(elementElement) {
    this.router.navigate(['/pages/chaines/chaine'], {queryParams: {id: elementElement}});
  }
}
