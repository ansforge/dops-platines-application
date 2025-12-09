///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {Component, reflectComponentType, ViewChild} from '@angular/core';
import {Router} from '@angular/router';
import {SessionService} from '../../../../services/session.service';
import {DatePipe} from '@angular/common';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {ProfileService} from '../../../../services/profile.service';
import {UsersService} from '../../../../services/users.service';
import {TABLE_CONSTANT, TABLE_TEXT} from '../../../entity/constant';
import {MatTableDataSource} from "@angular/material/table";
import {SelectionModel} from "@angular/cdk/collections";
import {MatSort} from "@angular/material/sort";
import {TranslateService} from "@ngx-translate/core";
import {MatPaginator} from "@angular/material/paginator";
import {ContextStateService} from "../../../../services/context-state.service";

@Component({
  selector: 'gestionSession',
  templateUrl: './gestionSession.html',
  styleUrls: ['./gestionSession.scss'],
})
export class GestionSessionComponent {


  @ViewChild(MatSort) sort: MatSort;

  dataSource: MatTableDataSource<any> = new MatTableDataSource();
  selection: SelectionModel<any> = new SelectionModel<any>(true, []);
  autoColumns: string[] = ['description'];
  allColumns: string[] = ['user', 'creationDate', 'application', ...this.autoColumns, 'rolePlateforme', 'sessionStatus', 'executionDate', 'actions'];
  labelsPath: string = "pages.sessions.gestion.labels";
  dateFormat: string = "dd/MM/yyyy HH:mm:ss"
  @ViewChild(MatPaginator) paginator: MatPaginator;
  protected readonly Date = Date;

  constructor(private sessionService: SessionService, private router: Router,
              private datePipe: DatePipe, private modalService: NgbModal,
              private profileService: ProfileService, private userService: UsersService,
              private translationService: TranslateService,
              private contextStateService: ContextStateService) {

  }

  ngOnInit() {
    this.loadSessions();
  }

  ngOnDestroy(){
    this.contextStateService.persistContext(this.paginator.pageIndex, this.paginator.pageSize, this.sort.active, this.sort.direction, window.scrollY, reflectComponentType(GestionSessionComponent).selector);
  }

  ngAfterViewInit() {
    this.dataSource.sortingDataAccessor = (data: any, sortHeaderId: string): string => {
      if (sortHeaderId === 'application') {
        return data['application']['name'];
      }
      if (sortHeaderId === 'user') {
        return data['application']['user']['forename'] + ' ' + data['application']['user']['name'];
      }
      if (sortHeaderId === 'rolePlateforme') {
        return this.translationService.instant(this.labelsPath + '.roles.' + data['application']['role'])
      }
      if (sortHeaderId === 'sessionStatus') {
        return this.translationService.instant(this.labelsPath + '.statuses.' + data['sessionStatus'])
      }
      if (sortHeaderId === 'executionDate') {
        return (Date.now() - data['executionDate']).toString();
      }
      if (typeof data[sortHeaderId] === 'string') {
        return data[sortHeaderId].toLocaleLowerCase();
      }
      return data[sortHeaderId];
    };
  }

  stop(idSession) {

    this.sessionService.stopSession(idSession).subscribe(res => {
      this.loadSessions();
    });

  }

  loadSessions() {
    this.sessionService.getActiveSessions().subscribe(session => {
      this.dataSource.data = session;
      this.contextStateService.restorePaginationPosition(this.dataSource, this.paginator, reflectComponentType(GestionSessionComponent).selector);
      this.contextStateService.restoreSort(this.dataSource, this.sort, reflectComponentType(GestionSessionComponent).selector);
      setTimeout(() => this.contextStateService.refreshPaginator(this.dataSource));
    });
  }

  transformDate(date: string) {
    if (date != null) {
      return Date.parse(date);
    } else return date;
  }

  diffDate(date1, date2) {
    if (date1 == null || date2 == null) {
      return "N/A";
    }
    let tmp = date2 - date1;
    let duration = "";

    tmp = Math.floor(tmp / 1000);
    const seconds = tmp % 60;
    if (seconds == 1) {
      duration = '1 seconde';
    } else {
      duration = seconds + ' secondes';
    }

    if (tmp >= 60) {
      tmp = Math.floor((tmp - seconds) / 60);
      const minutes = tmp % 60;
      if (minutes == 1) {
        duration = '1 minute';
      } else {
        duration = minutes + ' minutes';
      }

      if (tmp >= 60) {
        tmp = Math.floor((tmp - minutes) / 60);
        const hours = tmp % 24;
        if (hours == 1) {
          duration = '1 heure';
        } else {
          duration = hours + ' heures';
        }
        //console.log("tmp heure : " + tmp)
        if (tmp >= 24) {
          tmp = Math.floor((tmp - hours) / 24);
          const date = tmp % 365;
          if (date == 1) {
            duration = '1 jour';
          } else {
            duration = date + ' jours';
          }

          if (tmp >= 365) {
            tmp = Math.floor((tmp - date) / 365);
            const years = tmp;
            if (years <= 1) {
              duration = '1 an';
            } else {
              duration = years + ' ans';
            }

          }
        }
      }
    }
    return duration;
  }

  setAll(checked: boolean) {
    this.dataSource.data.forEach(element => {
      this.selection.setSelection(element, checked);
    });
  }

  setRow(element) {
    this.selection.toggle(element);
  }

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
  }
}
