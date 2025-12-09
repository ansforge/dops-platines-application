///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {Component, reflectComponentType, ViewChild} from '@angular/core';
import {UsersService} from '../../../../services/users.service';
import {NotifParams} from '../../../entity/notifparams';
import {HttpClient} from "@angular/common/http";
import {Router} from '@angular/router';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {ConfirmModalComponent} from '../../../ui/components/confirmModal/confirmModal.component';
import {ProfileService} from '../../../../services/profile.service';
import {FilterListService} from '../../../../services/filterlist.service';
import {MatTableDataSource} from "@angular/material/table";
import {SelectionModel} from "@angular/cdk/collections";
import {MatPaginator} from "@angular/material/paginator";
import {MatSort, Sort} from "@angular/material/sort";
import {TranslateService} from "@ngx-translate/core";
import {ContextStateService} from "../../../../services/context-state.service";

@Component({
  selector: 'listusers',
  templateUrl: './listusers.html',
  styleUrls: ['./listusers.scss'],
})
export class ListUsersComponent {
  @ViewChild(MatPaginator) paginator: MatPaginator;
  @ViewChild(MatSort) sort: MatSort;
  dataSource = new MatTableDataSource();
  selection = new SelectionModel<any>(true, []);
  notifParams: NotifParams = new NotifParams();
  autoColumns: string[] = ['forename', 'name', 'firm', 'mail']
  allColumns: string[] = [...this.autoColumns, 'profile', 'actions', 'checkbox'];
  isAdmin = false;
  labelsPath: string = "pages.users.labels";
  metadata = reflectComponentType(ListUsersComponent);

  constructor(private http: HttpClient,
              private router: Router,
              private service: UsersService,
              /*private _service: NotificationsService,*/
              private modalService: NgbModal,
              private profileService: ProfileService,
              private filterListService: FilterListService,
              private translateService: TranslateService,
              protected contextStateService: ContextStateService) {
  }

  ngOnInit() {
    this.service.getAllUsers().subscribe(data => {
      if (this.isAdmin) {
        this.dataSource.data = data
      } else {
        this.dataSource.data = data.filter(u => u.profile.label === "user");
      }
      this.contextStateService.restoreContext(
        this.dataSource,
        this.sort,
        this.paginator,
        this.metadata.selector
      );
      setTimeout(() => this.contextStateService.refreshPaginator(this.dataSource));
    }, err => {
      // this._service.error("Erreur de chargement", "Erreur lors du chargement de la liste des utilisateurs", this.notifParams.notif)
    });

    this.dataSource.filterPredicate = (data: any, filter: string): boolean => {
      return data.name?.toLowerCase().includes(filter) ||
        data.forename?.toLowerCase().includes(filter) ||
        data.firm?.toLowerCase().includes(filter) ||
        data.mail?.toLowerCase().includes(filter) ||
        (this.translateService.instant(this.labelsPath + "." + data.profile?.label)).toLowerCase().includes(filter);
    }
    this.dataSource.sortingDataAccessor = (data: any, sortHeaderId: string): string => {
      if (sortHeaderId=='profile') {
        return this.translateService.instant(this.labelsPath+ "." +data[sortHeaderId]['label']).toLocaleLowerCase();
      }
      if (typeof data[sortHeaderId] === 'string') {
        return data[sortHeaderId].toLocaleLowerCase();
      }
      return data[sortHeaderId];
    };
    if (this.profileService.getProfileFromToken() === "admin") {
      this.isAdmin = true;
    }
  }

  ngOnDestroy() {
    this.contextStateService.persistContext(this.paginator.pageIndex, this.paginator.pageSize, this.sort.active, this.sort.direction, window.scrollY, this.metadata.selector);
  }

  ngAfterViewInit() {
  }

  createUser(): void {
    this.router.navigate(['/pages/users/user']);
  }

  editUser(userId: number) {
    this.router.navigate(['/pages/users/user'], {queryParams: {id: userId}});
  }

  /* onUserRowSelect(id) {
    this.router.navigate(['/pages/users/user'], { queryParams: { id: id } });
  } */

  deleteUser() {
    if (this.selection.selected.length > 0) {
      const activeModal = this.modalService.open(ConfirmModalComponent, {size: 'lg'});
      let supressUser = this.filterListService.filterListForDelete(this.selection.selected, this.dataSource.data);
      activeModal.componentInstance.modalHeader = `Suppression d'un utilisateur`;
      activeModal.componentInstance.modalContent =
        `Êtes-vous sûr de vouloir supprimer les utilisateurs sélectionnés (${supressUser.length}) ?`;
      activeModal.result.then((res) => {
        if (res === 'confirm') {

          for (let i = supressUser.length - 1; i >= 0; i--) {
            const obj = supressUser[i];
            const obj2 = JSON.parse(JSON.stringify(supressUser[i]));
            if (obj2.mail === this.profileService.getJwtToken().sub) {
              // this._service.error('Suppression d\'utilisateur', 'Vous ne pouvez pas supprimer votre propre compte', this.notifParams);
            } else {
              this.service.deleteUser(obj2.id).subscribe(result => {
                this.dataSource.data = this.dataSource.data.filter(u => u['id'] !== obj2.id);
                setTimeout(() => this.contextStateService.refreshPaginator(this.dataSource));
                // this._service.success('Suppression d\'utilisateur', 'Utilisateur supprimé avec succès', this.notifParams);
              }, err => {
              });
            }

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

  isAllSelected() {
    const numSelected = this.selection.selected.length;
    const numRows = this.dataSource.data.length;
    return numSelected === numRows;
  }

  applyFilter() {
    this.dataSource.filter = this.contextStateService.filters.get(this.metadata.selector);
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
    if (a?.label && b?.label) {
      if (isAsc) {
        return this.translateService.instant(this.labelsPath+"."+a.label).localeCompare(this.translateService.instant(this.labelsPath+"."+b.label), 'en', {numeric: true});
      } else {
        return this.translateService.instant(this.labelsPath+"."+b.label).localeCompare(this.translateService.instant(this.labelsPath+"."+a.label), 'en', {numeric: true});
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
