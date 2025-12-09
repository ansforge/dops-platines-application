///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {Component, QueryList, ViewChildren} from '@angular/core';
import {UsersService} from '../../../../services/users.service';
import {HttpClient} from "@angular/common/http";
import {ActivatedRoute, Router} from '@angular/router';
import {ConfirmModalComponent} from '../../../ui/components/confirmModal/confirmModal.component';
import {MatTableDataSource} from "@angular/material/table";
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {FilterListService} from '../../../../services/filterlist.service';
import {ProjectLibraryService} from '../../../../services/projectlibrary.service';
import {User} from '../../../entity/user';
import {Theme} from '../../../entity/theme';
import {ProfileService} from '../../../../services/profile.service';
import {SelectionModel} from "@angular/cdk/collections";
import {MatPaginator} from "@angular/material/paginator";
import {Sort} from "@angular/material/sort";
import {ContextStateService} from "../../../../services/context-state.service";
import {PlatineModalService} from "../../../ui/components/modal/platine-modal.service";
import {TranslateService} from "@ngx-translate/core";

@Component({
  selector: 'usersFamilles',
  templateUrl: './usersFamilles.html',
  styleUrls: ['./usersFamilles.scss'],
})
export class UsersFamillesComponent {

  @ViewChildren(MatPaginator) paginator = new QueryList<MatPaginator>();

  assignedUsersDataSource: MatTableDataSource<any> = new MatTableDataSource();
  assignedUsersAutoColumns: string[] = ['name', 'mail'];
  assignedUsersAllColumns: string[] = [...this.assignedUsersAutoColumns, 'checkbox'];
  assignedUsersSelection = new SelectionModel<any>(true, []);
  unassignedUsersDataSource: MatTableDataSource<any> = new MatTableDataSource();
  unassignedUsersAutoColumns: string[] = ['name', 'mail'];
  unassignedUsersAllColumns: string[] = [...this.unassignedUsersAutoColumns, 'checkbox'];
  unassignedUsersSelection = new SelectionModel<any>(true, []);

  isAdmin = false;
  users: Array<User>;
  family: Theme;
  assignedUsers: Array<User> = [];
  unassignedUsers: Array<User> = [];
  touchedUsers: Array<User> = [];
  labelsPath: string = "pages.user-families.labels";

  constructor(private route: ActivatedRoute,
              private profileService: ProfileService,
              private projectLibraryService: ProjectLibraryService,
              private http: HttpClient,
              private router: Router,
              private service: UsersService,
              private modalService: NgbModal,
              private filterListService: FilterListService,
              private contextStateService: ContextStateService,
              private platinesModalService: PlatineModalService,
              private translateService: TranslateService) {
  }

  ngOnInit() {

    this.assignedUsersDataSource.sortingDataAccessor = (data: any, sortHeaderId: string): string => {
      if (typeof data[sortHeaderId] === 'string') {
        return data[sortHeaderId].toLocaleLowerCase();
      }
      return data[sortHeaderId];
    };

    this.unassignedUsersDataSource.sortingDataAccessor = (data: any, sortHeaderId: string): string => {
      if (typeof data[sortHeaderId] === 'string') {
        return data[sortHeaderId].toLocaleLowerCase();
      }
      return data[sortHeaderId];
    }


    if (this.profileService.getProfileFromToken() === "admin") {
      this.isAdmin = true;
    }

    this.route.queryParams.subscribe(params => {
      this.projectLibraryService.getFamilyById(params.id).subscribe({
        next: res => {
          this.family = res;
          if (!this.isAdmin) {
            this.service.safeFetchAllUnassignedUsers().subscribe({
              next: data => {
                this.unassignedUsers = data.filter(u => u.profile.label === "user");
                this.unassignedUsersDataSource.data = this.unassignedUsers;
                setTimeout(() => this.contextStateService.refreshPaginator(this.unassignedUsersDataSource));
              },
              error: () => {
                this.platinesModalService.openErrorModalWithCustomMessage({
                  modalHeader: this.translateService.instant("pages.user-families.errors.users_title"),
                  modalContent: this.translateService.instant("pages.user-families.errors.users_msg")
                })
              }});
            this.service.getAllUsers().subscribe({
              next: data => {
                this.assignedUsers = data.filter(u => u.profile.label === "user" && u.families.includes(u.families.find(f => f.id == this.family.id)));
                this.assignedUsersDataSource.data = this.assignedUsers;
                setTimeout(() => this.contextStateService.refreshPaginator(this.assignedUsersDataSource));
              },
              error: () => {
                this.platinesModalService.openErrorModalWithCustomMessage({
                  modalHeader: this.translateService.instant("pages.user-families.errors.users_title"),
                  modalContent: this.translateService.instant("pages.user-families.errors.users_msg")
                })
              }
            })
          } else {
            this.service.getAllUsers().subscribe({
              next: data => {
                this.users = data.filter(u => u.profile.label === "user");
                this.unassignedUsers = this.users;
                this.assignedUsers = this.users.filter(u => u.families.includes(u.families.find(f => f.id == this.family.id)));
                this.unassignedUsers = this.users.filter(u => !this.assignedUsers.includes(u));
                this.assignedUsersDataSource.data = this.assignedUsers;
                this.unassignedUsersDataSource.data = this.unassignedUsers;
                setTimeout(() => this.contextStateService.refreshPaginator(this.assignedUsersDataSource));
                setTimeout(() => this.contextStateService.refreshPaginator(this.unassignedUsersDataSource));
              },
              error: () => {
                this.platinesModalService.openErrorModalWithCustomMessage({
                  modalHeader: this.translateService.instant("pages.user-families.errors.users_title"),
                  modalContent: this.translateService.instant("pages.user-families.errors.users_msg")
                })
              }
            });
          }
        }, error:()=>{
          void this.router.navigate(['/familles']);
        }
      })
    });
  }

  ngAfterViewInit() {
    this.assignedUsersDataSource.paginator = this.paginator.toArray()[1];
    this.unassignedUsersDataSource.paginator = this.paginator.toArray()[0];
  }

  arrayRemove(arr, value) {
    return arr.filter(ele => ele != value);
  }

  assign() {
    let toBeAssigned: User[] = this.unassignedUsersSelection.selected;
    for (const user of toBeAssigned) {
      user.families = user.families.concat(this.family);
      this.touchedUsers = this.touchedUsers.concat(user);
      this.unassignedUsersDataSource.data = this.unassignedUsersDataSource.data.filter(u => u['id'] !== user.id);
      setTimeout(() => this.contextStateService.refreshPaginator(this.unassignedUsersDataSource));
      this.unassignedUsersSelection.deselect(user);
      this.assignedUsersDataSource.data = this.assignedUsersDataSource.data.concat(user);
      setTimeout(() => this.contextStateService.refreshPaginator(this.assignedUsersDataSource));
      this.assignedUsersSelection.select(user);
    }
  }

  unassign() {
    let toBeUnassigned: User[] = this.assignedUsersSelection.selected;
    for (const user of toBeUnassigned) {
      user.families = user.families.filter(f => f.id !== this.family.id);
      this.touchedUsers = this.touchedUsers.concat(user);
      this.assignedUsersDataSource.data = this.assignedUsersDataSource.data.filter(u => u['id'] !== user.id);
      setTimeout(() => this.contextStateService.refreshPaginator(this.assignedUsersDataSource));
      this.assignedUsersSelection.deselect(user);
      this.unassignedUsersDataSource.data = this.unassignedUsersDataSource.data.concat(user);
      this.unassignedUsersSelection.select(user);
    }
  }

  validate() {
    if (this.touchedUsers.length > 0) {
      const activeModal = this.modalService.open(ConfirmModalComponent, {size: 'lg'});
      activeModal.componentInstance.modalHeader = 'Affectation d\'utilisateurs';
      activeModal.componentInstance.modalContent = 'Êtes-vous sûr de vouloir performer l\'affectation des utilisateurs ?';
      activeModal.result.then((res) => {
        if (res === 'confirm') {
          this.touchedUsers.forEach(user =>
            this.service.updateUserFamilies(user).subscribe({
              next: () => {
                void this.router.navigate(['/pages/familles/listFamilles']);
              },
              error: () => {
                this.platinesModalService.openErrorModalWithCustomMessage({
                  modalHeader: this.translateService.instant("pages.user-families.errors.assignment_title"),
                  modalContent: this.translateService.instant("pages.user-families.errors.assignment_msg")
                });
              }
            })
          );
        }
      })
    }
  }

  setAllUnassigned($event) {
    if (this.isAllUnassignedUsersSelected() || this.unassignedUsersSelection.hasValue()) {
      this.unassignedUsersSelection.clear();
      $event.target.checked = false;
    } else {
      this.unassignedUsersDataSource.data.forEach(row => this.unassignedUsersSelection.select(row));
    }
  }

  isAllUnassignedUsersSelected() {
    const numSelected = this.unassignedUsersSelection.selected.length;
    const numRows = this.unassignedUsersDataSource.data.length;
    if(numRows > 0) {
      return numSelected === numRows;
    } else {
      return false;
    }
  }

  setRowUnassigned(element) {
    this.unassignedUsersSelection.toggle(element);
  }

  setAllAssigned($event) {
    if (this.isAllAssignedUsersSelected() || this.assignedUsersSelection.hasValue()) {
      this.assignedUsersSelection.clear();
      $event.target.checked = false;
    } else {
      this.assignedUsersDataSource.data.forEach(row => this.assignedUsersSelection.select(row));
    }
  }

  isAllAssignedUsersSelected() {
    const numSelected = this.assignedUsersSelection.selected.length;
    const numRows = this.assignedUsersDataSource.data.length;
    if(numRows > 0) {
      return numSelected === numRows;
    } else {
      return false;
    }
  }

  setRowAssigned(element) {
    this.assignedUsersSelection.toggle(element);

  }

  sortAssignedData(sort: Sort) {
    const sortedData = this.assignedUsersDataSource.data.slice();
    sortedData.sort(
      (a, b) => sort.direction === 'asc' ?
        this.sortAlphanumeric(a[sort.active], b[sort.active], true) :
        this.sortAlphanumeric(a[sort.active], b[sort.active], false));
    this.assignedUsersDataSource.data = sortedData;
  }

  sortUnassignedData(sort: Sort) {
    const sortedData = this.unassignedUsersDataSource.data.slice();
    sortedData.sort(
      (a, b) => sort.direction === 'asc' ?
        this.sortAlphanumeric(a[sort.active], b[sort.active], true) :
        this.sortAlphanumeric(a[sort.active], b[sort.active], false));
    this.unassignedUsersDataSource.data = sortedData;
  }

  sortAlphanumeric(a: string, b: string, isAsc: boolean): number {
    return isAsc ? a.localeCompare(b, 'en', {numeric: true}) : b.localeCompare(a, 'en', {numeric: true});
  }
}
