///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {Component, OnInit, reflectComponentType, ViewChild} from '@angular/core';
import {Router} from '@angular/router';
import {ProjectLibraryService} from '../../../../services/projectlibrary.service';
import {Theme} from '../../../entity/theme';
import {NotifParams} from '../../../entity/notifparams';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {MatTableDataSource} from "@angular/material/table";
import {MatPaginator} from "@angular/material/paginator";
import {MatSort, Sort} from "@angular/material/sort";
import {ContextStateService} from "../../../../services/context-state.service";
import { CURRENT_USER_KEY } from '../../../../services/authentication.service';


@Component({
  selector: 'listSystemes',
  templateUrl: './listSystemes.html',
  styleUrls: ['./listSystemes.scss'],
})
export class ListSystemesComponent implements OnInit {

  @ViewChild(MatPaginator) paginator: MatPaginator;
  @ViewChild(MatSort) sort: MatSort;

  dataSource: MatTableDataSource<any> = new MatTableDataSource();
  autoColumns: string[] = ['name'];
  allColumns: string[] = ['family', ...this.autoColumns, 'actions'];

  notifParams = new NotifParams();
  userStock = JSON.parse(localStorage.getItem(CURRENT_USER_KEY));// TODO : revoir ceci pour utiliser l'objet Profile de {@link AuthenticationService}
  families: Theme[];

  labelsPath: string = "pages.systems.labels";
  systemsFamily: any;

  metadata = reflectComponentType(ListSystemesComponent);

  constructor(private projectLibraryService: ProjectLibraryService,
              private modalService: NgbModal,
              private router: Router,
              protected contextStateService: ContextStateService) {
  }

  ngOnInit() {
    this.systemsFamily = this.contextStateService.systemsFamily;
    this.dataSource.filterPredicate = function (data: any, filter: string): boolean {
      return data.family?.name?.toLowerCase().includes(filter) ||
        data.name?.toLowerCase().includes(filter)
    }
    this.dataSource.sortingDataAccessor = (data: any, sortHeaderId: string): string => {
      if (sortHeaderId == 'family.name') {
        return data[sortHeaderId]['family']['name'].toLocaleLowerCase();
      }
      if (typeof data[sortHeaderId] === 'string') {
        return data[sortHeaderId].toLocaleLowerCase();
      }
      return data[sortHeaderId];
    };

    this.projectLibraryService.getSystems()
      .subscribe(res => {
        this.dataSource.data = res;
        if (this.systemsFamily != "null") {
          this.filterSystems();
        }
        this.contextStateService.restoreContext(
          this.dataSource,
          this.sort,
          this.paginator,
          this.metadata.selector
        )
        setTimeout(() => this.contextStateService.refreshPaginator(this.dataSource));
      });
    this.projectLibraryService.getFamilies().subscribe(res => {
      this.families = res;
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
    this.contextStateService.systemsFamily = this.systemsFamily;
  }

  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
  }

  filterSystems() {
    if (this.systemsFamily === 'null') {
      this.projectLibraryService.getSystems()
        .subscribe(res => {
          this.dataSource.data = res;
          setTimeout(() => this.contextStateService.refreshPaginator(this.dataSource));
        });
    } else {
      this.projectLibraryService.getSystemByFamilyId(this.systemsFamily).subscribe(results => {
        this.dataSource.data = results;
        setTimeout(() => this.contextStateService.refreshPaginator(this.dataSource));
      }, err => {
        // this._service.error(ERROR_CONSTANT.ERROR_MESSAGE_TITLE, ERROR_CONSTANT.ERROR_MESSAGE_ALL_SYSTEMS, this.notifParams.notif);
      });
    }
  }

  addSystem() {
    this.router.navigate(['/pages/systemes/systeme']);
  }

  edit(id) {
    this.router.navigate(['/pages/systemes/systeme'], {queryParams: {id: id}});
  }

  applyFilter() {
    this.dataSource.filter = this.contextStateService.filters.get(this.metadata.selector)?.trim().toLowerCase();
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
    if (a?.name && b?.name) {
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

