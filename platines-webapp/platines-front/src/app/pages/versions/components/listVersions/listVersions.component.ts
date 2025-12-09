///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {Component, OnInit, reflectComponentType, ViewChild} from '@angular/core';
import {Router} from '@angular/router';
import {NotifParams} from '../../../entity/notifparams';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {ProjectLibraryService} from '../../../../services/projectlibrary.service';
import {Theme} from '../../../entity/theme';
import {ProfileService} from '../../../../services/profile.service';
import {MatTableDataSource} from "@angular/material/table";
import {MatPaginator} from "@angular/material/paginator";
import {MatSort, Sort} from "@angular/material/sort";
import {ContextStateService} from "../../../../services/context-state.service";

@Component({
  selector: 'listVersions',
  templateUrl: './listVersions.html',
  styleUrls: ['./listVersions.scss'],
})
export class ListVersionsComponent implements OnInit {
  @ViewChild(MatPaginator) paginator: MatPaginator;
  @ViewChild(MatSort) sort: MatSort;
  notifParams = new NotifParams();
  dataSource: MatTableDataSource<any> = new MatTableDataSource();
  autoColumns: string[] = ['label', 'description'];
  allColumns: string[] = ['family', 'service', ...this.autoColumns, 'visibility', 'actions'];
  isAdmin = false;
  isManager = false;
  families: Theme[];
  labelsPath: string = "pages.versions.labels";
  versionsFamily: any;
  metadata = reflectComponentType(ListVersionsComponent);

  constructor(private projectLibraryService: ProjectLibraryService,
              private router: Router,
              private modalService: NgbModal,
              private profileService: ProfileService,
              protected contextStateService: ContextStateService) {
    if (this.profileService.getProfileFromToken() === "admin") {
      this.isAdmin = true;
    } else if (this.profileService.getProfileFromToken() === "manager") {
      this.isManager = true;
    }
  }

  ngOnInit() {
    this.dataSource.filterPredicate = function (data: any, filter: string): boolean {
      return data.service?.theme?.name?.toLowerCase().includes(filter) ||
        data.service?.name?.toLowerCase().includes(filter) ||
        data.label?.toLowerCase().includes(filter) ||
        data.description?.toLowerCase().includes(filter);
    }
    this.projectLibraryService.getAllVersions()
      .subscribe(res => {
        this.dataSource.data = res;
        this.contextStateService.restoreContext(
          this.dataSource,
          this.sort,
          this.paginator,
          this.metadata.selector
        );
        setTimeout(() => this.contextStateService.refreshPaginator(this.dataSource));
        this.versionsFamily = this.contextStateService.versionsFamily;
      });

    this.projectLibraryService.getFamilies().subscribe(res => {
      this.families = res;
      if (this.versionsFamily != "null") {
        this.filterVersions();
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
    this.contextStateService.versionsFamily = this.versionsFamily;
  }

  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
  }

  filterVersions() {
    if (this.versionsFamily === 'null') {
      this.projectLibraryService.getAllVersions().subscribe(res => {
        this.dataSource.data = res;
        setTimeout(() => this.contextStateService.refreshPaginator(this.dataSource));
      });
    } else {
      this.projectLibraryService.getVersionsByFamilyId(this.versionsFamily).subscribe(res => {
        this.dataSource.data = res;
        setTimeout(() => this.contextStateService.refreshPaginator(this.dataSource));
      });
    }
  }


  edit(row) {
    this.router.navigate(['/pages/versions/version'], {queryParams: {id: row.id}});
  }

  addVersion() {
    this.router.navigate(['/pages/versions/version']);
  }

  applyFilter() {
    this.dataSource.filter = this.contextStateService.filters.get(this.metadata.selector)?.trim().toLowerCase();
  }

  sortData(sort: Sort) {
    const sortedData = this.dataSource.data.slice();
    if (sort.active === 'service') {
      sortedData.sort(
        (a, b) => sort.direction === 'asc' ?
          this.sortAlphanumeric(a[sort.active].name, b[sort.active].name, true) :
          this.sortAlphanumeric(a[sort.active].name, b[sort.active].name, false));
    } else if (sort.active !== 'family') {
      sortedData.sort(
        (a, b) => sort.direction === 'asc' ?
          this.sortAlphanumeric(a[sort.active], b[sort.active], true) :
          this.sortAlphanumeric(a[sort.active], b[sort.active], false));
    } else {
      sortedData.sort(
        (a, b) => sort.direction === 'asc' ?
          this.sortAlphanumeric(a.service.theme, b.service.theme, true) :
          this.sortAlphanumeric(a.service.theme, b.service.theme, false)
      )
    }
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
    } else if (a?.name && b?.name) {
      if (isAsc) {
        return a.name.localeCompare(b.name, 'en', {numeric: true});
      } else {
        return b.name.localeCompare(a.name, 'en', {numeric: true});
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

