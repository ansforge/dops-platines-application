///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {Component, reflectComponentType, ViewChild} from '@angular/core';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {Router} from '@angular/router';
import {Theme} from '../../../entity/theme';
import {ProjectLibraryService} from '../../../../services/projectlibrary.service';
import {NotifParams} from '../../../entity/notifparams';
import {FilterListService} from '../../../../services/filterlist.service';
import {ProfileService} from '../../../../services/profile.service';
import {MatTableDataSource} from "@angular/material/table";
import {SelectionModel} from "@angular/cdk/collections";
import {MatPaginator} from "@angular/material/paginator";
import {MatSort, Sort} from "@angular/material/sort";
import {ContextStateService} from "../../../../services/context-state.service";
import {PlatineModalService} from "../../../ui/components/modal/platine-modal.service";
import {TranslateService} from "@ngx-translate/core";
import { CURRENT_USER_KEY } from '../../../../services/authentication.service';

@Component({
  selector: 'listFamilles',
  templateUrl: './listFamilles.html',
  styleUrls: ['./listFamilles.scss'],
})
export class ListFamillesComponent {

  isAdmin = false;
  family: Theme;
  dataSource: MatTableDataSource<any> = new MatTableDataSource();
  selection: SelectionModel<any> = new SelectionModel<any>(true, []);
  autoColumns: string[] = ['name'];
  allColumns: string[] = [...this.autoColumns, 'visibility', 'actions'];

  @ViewChild(MatPaginator) paginator: MatPaginator;
  @ViewChild(MatSort) sort: MatSort;


  notifParams = new NotifParams();
  userStock = JSON.parse(localStorage.getItem(CURRENT_USER_KEY));//TODO review this to use the Profile object from AuthenticationService
  labelsPath: string = "pages.families.labels";
  metadata = reflectComponentType(ListFamillesComponent);

  constructor(private profileService: ProfileService,
              private projectLibraryService: ProjectLibraryService,
              private router: Router,
              private modalService: NgbModal,
              private platinesModalService: PlatineModalService,
              private filterListService: FilterListService,
              protected contextStateService: ContextStateService,
              private translateService: TranslateService
  ) {
  }

  ngOnInit() {
    this.dataSource.filterPredicate = function (data: any, filter: string): boolean {
      return data.name?.toLowerCase().includes(filter);
    }
    this.dataSource.sortingDataAccessor = (data: any, sortHeaderId: string): string => {
      if (typeof data[sortHeaderId] === 'string') {
        return data[sortHeaderId].toLocaleLowerCase();
      }
      return data[sortHeaderId];
    };

    if (this.profileService.getProfileFromToken() === "admin") {
      this.isAdmin = true;
    }

    this.projectLibraryService.getFamilies().subscribe({
      next: res => {
        this.dataSource.data = res;
        this.contextStateService.restoreContext(
          this.dataSource,
          this.sort,
          this.paginator,
          this.metadata.selector
        )
        setTimeout(() => this.contextStateService.refreshPaginator(this.dataSource));
      },
      error: () => {
        this.platinesModalService.openErrorModalWithCustomMessage({
          modalHeader: this.translateService.instant('pages.families.errors.families_title'),
          modalContent: this.translateService.instant('pages.families.errors.families_msg')
        })
      }
    });
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

  sortData(sort: Sort) {
    const sortedData = this.dataSource.data.slice();
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

  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
  }

  edit(row) {
    void this.router.navigate(['/pages/familles/famille'], {queryParams: {id: row.id}});
  }

  addFamily(): void {
    void this.router.navigate(['/pages/familles/famille'], {queryParams: {new: true}});
  }

  setAll(checked: boolean) {
    this.dataSource.data.forEach(element => {
      this.selection.setSelection(element, checked);
    });
  }

  setRow(element) {
    this.selection.toggle(element);
  }

  applyFilter() {
    this.dataSource.filter = this.contextStateService.filters.get(this.metadata.selector)?.trim().toLowerCase();
  }

  editFamily(element) {
    void this.router.navigate(['/pages/familles/famille'], {queryParams: {id: element}});
  }

  assignUsers(element) {
    void this.router.navigate(['/pages/familles/usersFamilles'], {queryParams: {id: element}});
  }
}
