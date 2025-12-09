///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {Component, OnInit, reflectComponentType, ViewChild} from '@angular/core';
import {DatePipe} from '@angular/common';
import {ProjectLibraryService} from '../../../../services/projectlibrary.service';
import {Router} from '@angular/router';
import {MODAL_CONSTANT} from '../../../entity/constant';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {ConfirmModalComponent} from '../../../ui/components/confirmModal/confirmModal.component';
import {NotifParams} from '../../../entity/notifparams';
import {FilterListService} from '../../../../services/filterlist.service';
import {MatTableDataSource} from "@angular/material/table";
import {SelectionModel} from "@angular/cdk/collections";
import {MatSort, Sort} from "@angular/material/sort";
import {MatPaginator} from "@angular/material/paginator";
import {TranslateService} from "@ngx-translate/core";
import {PlatineModalService} from "../../../ui/components/modal/platine-modal.service";
import {ContextStateService} from "../../../../services/context-state.service";
import { CURRENT_USER_KEY } from '../../../../services/authentication.service';

export interface Certificate {
  fileName: string;
  description: string;
  state: string;
  downloadable: boolean;
  validityDate: string;
  id: number;
  checkbox: boolean;
}

@Component({
  selector: 'listCertificats',
  templateUrl: './listCertificats.html',
  styleUrls: ['./listCertificats.scss'],
})
export class ListCertificatsComponent implements OnInit {
  @ViewChild(MatPaginator) paginator: MatPaginator;
  @ViewChild(MatSort) sort: MatSort;
  dataSource = new MatTableDataSource();
  selection = new SelectionModel(true, []);
  autoColumns: string[] = ['fileName', 'description'];
  allColumns: string[] = [...this.autoColumns, 'type', 'state', 'validityDate', 'downloadable', 'actions', 'checkbox'];
  labelsPath: string = "pages.certificates.labels";
  dateFormat: string = "dd/MM/yyyy HH:mm:ss";

  notifParams: NotifParams = new NotifParams();
  userStock = JSON.parse(localStorage.getItem(CURRENT_USER_KEY));//TODO review this to use the Profile object from AuthenticationService

  metadata = reflectComponentType(ListCertificatsComponent);

  constructor(private projectLibraryService: ProjectLibraryService,
              private router: Router,
              private datePipe: DatePipe,
              private modalService: NgbModal,
              private filterListService: FilterListService,
              private translationService: TranslateService,
              protected contextStateService: ContextStateService,
              private platinesModalService: PlatineModalService) {
  }

  ngOnInit() {
    this.dataSource.filterPredicate = (data: any, filter: string) => {
      return data.fileName?.toLowerCase().includes(filter) ||
        data.description?.toLowerCase().includes(filter) ||
        (this.translationService.instant(this.labelsPath + ".types." + data.type))?.toLowerCase().includes(filter) ||
        (this.translationService.instant(this.labelsPath + ".states." + data.state))?.toLowerCase().includes(filter) ||
        (this.datePipe.transform(data.validityDate, this.dateFormat))?.toLowerCase().includes(filter);
    }
    this.projectLibraryService.getTestCertificates().subscribe({
      next: res => {
        this.dataSource.data = res;
        this.contextStateService.restoreContext(
          this.dataSource,
          this.sort,
          this.paginator,
          this.metadata.selector,
        )
        setTimeout(() => { this.contextStateService.refreshPaginator(this.dataSource)});
      },
      error: () => {
        this.platinesModalService.openErrorModalWithCustomMessage({
          modalHeader: this.labelsPath + ".errors.get_title",
          modalContent: this.labelsPath + ".errors.get_msg"
          }
        )
      }
    });
  }

  ngOnDestroy() {
    this.contextStateService.persistContext(this.paginator.pageIndex, this.paginator.pageSize, this.sort.active, this.sort.direction, window.scrollY, reflectComponentType(ListCertificatsComponent).selector);
  }

  ngAfterViewInit() {
  }

  addCertificate() {
    this.router.navigate(['/pages/certificats/certificat']);
  }

  edit(row) {
    this.router.navigate(['/pages/certificats/certificat'], {queryParams: {id: row}});
  }

  deleteCertificate() {
    if (this.selection.selected.length > 0) {
      let supressCertificats = this.filterListService.filterListForDelete(this.selection.selected, this.dataSource.data);
      const activeModal = this.modalService.open(ConfirmModalComponent, {size: 'lg'});
      activeModal.componentInstance.modalHeader = MODAL_CONSTANT.MODAL_HEADER_DELETE_CERTIF;
      activeModal.componentInstance.modalContent = `Êtes-vous sûr de vouloir supprimer les certificats sélectionnés (${supressCertificats.length}) ?`;
      activeModal.result.then((res) => {
        if (res === 'confirm') {
          let modalOpened: boolean = false;
          for (let i = supressCertificats.length - 1; i >= 0; i--) {
            let certif = supressCertificats[i];
            const certificate = JSON.parse(JSON.stringify(certif));
            this.projectLibraryService.deleteTestCertificate(certificate.id).subscribe({
              next: () => {
                this.dataSource.data = this.dataSource.data.filter(item => item['id'] !== certif['id']);
                setTimeout(() => {
                  this.contextStateService.refreshPaginator(this.dataSource)
                });
                this.selection.clear();
              },
              error: err => {
                if (!modalOpened) {
                  this.platinesModalService.openErrorModalWithCustomMessage({
                    modalHeader: this.labelsPath + ".errors.delete_title",
                    modalContent: this.labelsPath + ".errors.delete_msg_" + err.status
                  })
                  modalOpened = true;
                }
              }
            });
          }
        }
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

  applyFilter() {
    this.dataSource.filter = this.contextStateService.filters.get(this.metadata.selector)?.trim().toLowerCase();
  }

  isAllSelected() {
    const numSelected = this.selection.selected.length;
    const numRows = this.dataSource.data.length;
    return numSelected === numRows;
  }

  sortData(sort: Sort) {
    const sortedData: any[] = this.dataSource.data.slice();
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
    } else if (a?.label && b?.label) {
      if (isAsc) {
        return a.label.localeCompare(b.label, 'en', {numeric: true});
      } else {
        return b.label.localeCompare(a.label, 'en', {numeric: true});
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
}
