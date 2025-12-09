///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {Component, reflectComponentType, ViewChild} from '@angular/core';
import {ApplicationsService} from '../../../../services/application.service';
import {ProfileService} from '../../../../services/profile.service';
import {Router} from '@angular/router';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {ConfirmModalComponent} from '../../../ui/components/confirmModal/confirmModal.component';
import {NotifParams} from '../../../entity/notifparams'
import {SelectionModel} from "@angular/cdk/collections";
import {MatTableDataSource} from "@angular/material/table";
import {FilterListService} from '../../../../services/filterlist.service';
import {MatPaginator} from "@angular/material/paginator";
import {MatSort, Sort} from "@angular/material/sort";
import {TranslateService} from "@ngx-translate/core";
import {ContextStateService} from "../../../../services/context-state.service";
import {PlatineModalService} from "../../../ui/components/modal/platine-modal.service";

@Component({
  selector: 'listApplications',
  templateUrl: './listApplications.html',
  styleUrls: ['./listApplications.scss'],
})
export class ListApplicationsComponent {

  autoColumns: string[] = ['version', 'description'];
  allColumns: string[] = ['name', 'role', ...this.autoColumns, 'user', 'actions', 'checkbox'];
  dataSource = new MatTableDataSource();
  selection = new SelectionModel<any>(true, []);
  notifParams: NotifParams = new NotifParams();
  modal: ConfirmModalComponent;
  labelsPath: string = "pages.applications.labels";
  @ViewChild(MatPaginator) paginator: MatPaginator;
  @ViewChild(MatSort) sort: MatSort;
  metadata = reflectComponentType(ListApplicationsComponent);

  constructor(private translationService: TranslateService,
              private applicationsService: ApplicationsService,
              private router: Router,
              private modalService: NgbModal,
              private platineModalService: PlatineModalService,
              private profileService: ProfileService,
              private filterListService: FilterListService,
              protected contextStateService: ContextStateService) {
    let isAdmin = false;
    if (this.profileService.getProfileFromToken() != "user") {
      isAdmin = true;
    }
  }

  ngOnInit() {
    this.dataSource.filterPredicate = (data: any, filter: string) => {
      return (data.user?.forename + " " + data.user?.name).toLowerCase().includes(filter) ||
        data.name?.toLowerCase().includes(filter) ||
        (this.translationService.instant(this.labelsPath + ".roles." + data.role))?.toLowerCase().includes(filter) ||
        data.version?.toLowerCase().includes(filter) ||
        data.description?.toLowerCase().includes(filter);
    }
    this.loadApplication();
  }

  ngOnDestroy() {
    this.contextStateService.persistContext(
      this.paginator.pageIndex,
      this.paginator.pageSize,
      this.sort.active,
      this.sort.direction,
      window.scrollY,
      this.metadata.selector
    )
  }

  ngAfterViewInit() {
  }

  loadApplication() {
    this.applicationsService.getApplications().subscribe(data => {
      this.dataSource.data = data;
      this.contextStateService.restoreContext(
        this.dataSource,
        this.sort,
        this.paginator,
        this.metadata.selector
      )
      setTimeout(() => { this.contextStateService.refreshPaginator(this.dataSource)});
    });
  }

  deleteApplication() {
    let modalOpened = false;
    if (this.selection.selected.length > 0) {
      let supressApplications = this.filterListService.filterListForDelete(this.selection.selected, this.dataSource.data);
      const activeModal = this.modalService.open(ConfirmModalComponent, {size: 'lg'});
      activeModal.componentInstance.modalHeader = "Suppression d'une application";
      activeModal.componentInstance.modalContent =
        `Êtes-vous sûr de vouloir supprimer les applications sélectionnées (${supressApplications.length}) ?`;
      activeModal.result.then((res) => {
        if (res === 'confirm') {

          for (let i = supressApplications.length - 1; i >= 0; i--) {
            const obj = JSON.stringify(supressApplications[i]);
            this.applicationsService.deleteApp(JSON.parse(obj)).subscribe(() => {
              this.dataSource.data = this.dataSource.data.filter(app => app['id'] !== supressApplications[i].id);
              setTimeout(() => { this.contextStateService.refreshPaginator(this.dataSource)});
              this.selection.clear();
            }, err => {
              if (!modalOpened) {
                this.platineModalService.openErrorModalWithCustomMessage({
                  modalHeader: this.labelsPath + ".errors.header",
                  modalContent: this.labelsPath + ".errors."+err.status
                })
                modalOpened = true;
              }
            });
          }
        }
      });
    }
  }

  /**
   * Action réalisée lors de la sélection d'une ligne par l'utilisateur.
   * @param id identifiant de l'application sélectionnée.
   */
  onUserRowSelect(id): void {
    this.router.navigate(['/pages/applications/application'], {queryParams: {id: id}});
  }

  /**
   * Action réalisée lors du clique sur le bouton d'ajout d'une application.
   */
  createApplication() {
    this.router.navigate(['/pages/applications/application'], {queryParams: {new: true}});
  }

  isAllSelected() {
    const numSelected = this.selection.selected.length;
    const numRows = this.dataSource.data.length;
    return numSelected === numRows;
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


  sortData(sort: Sort) {
    const sortedData: any[] = this.dataSource.data.slice();
    sortedData.sort(
      (a, b) => sort.direction === 'asc' ?
        this.sortAlphanumeric(a[sort.active], b[sort.active], true) :
        this.sortAlphanumeric(a[sort.active], b[sort.active], false));
    this.dataSource.data = sortedData;
  }

  sortAlphanumeric(a, b, isAsc: boolean): number {
    if (a?.forename && b?.forename) {
      if (isAsc) {
        return (a.forename + " " + a.name).localeCompare((b.forename + " " + b.name), 'en', {numeric: true});
      } else {
        return (b.forename + " " + b.name).localeCompare((a.forename + " " + a.name), 'en', {numeric: true});
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
