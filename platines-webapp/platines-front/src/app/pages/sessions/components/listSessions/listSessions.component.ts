///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {Component, OnDestroy, reflectComponentType, ViewChild} from '@angular/core';
import {Router} from '@angular/router';
import {host} from '../../../../../environments/environment';
import {DatePipe} from '@angular/common';
import {ConfirmModalComponent} from '../../../ui/components/confirmModal/confirmModal.component';
import {NotifParams} from '../../../entity/notifparams';
import {MODAL_CONSTANT} from '../../../entity/constant';
import {SessionList} from '../../../entity/sessionslist';
import {SessionService} from '../../../../services/session.service';
import {ProfileService} from '../../../../services/profile.service';
import jwt_decode from "jwt-decode";
import * as Stomp from 'stompjs';
import * as SockJS from 'sockjs-client';
import {UsersService} from '../../../../services/users.service';
import {FilterListService} from '../../../../services/filterlist.service';
import {MatTableDataSource} from "@angular/material/table";
import {SelectionModel} from "@angular/cdk/collections";
import {MatPaginator} from "@angular/material/paginator";
import {MatSort, Sort} from "@angular/material/sort";
import {TranslateService} from "@ngx-translate/core";
import {ContextStateService} from "../../../../services/context-state.service";
import { PlatineModalService } from "../../../ui/components/modal/platine-modal.service";
import { CURRENT_USER_KEY } from '../../../../services/authentication.service';

const CREATION_DATE_COLUMN: string='creationDate';

@Component({
  selector: 'listSessions',
  templateUrl: './listSessions.html',
  styleUrls: ['./listSessions.scss'],

})
export class ListSessionsComponent implements OnDestroy {

  @ViewChild(MatPaginator) paginator: MatPaginator;
  @ViewChild(MatSort) sort: MatSort;

  dataSource: MatTableDataSource<SessionList> = new MatTableDataSource();
  selection: SelectionModel<any> = new SelectionModel<any>(true, []);
  autoColumns: string[] = ['description'];
  allColumns: string[] = ['user', CREATION_DATE_COLUMN, 'applicationName', ...this.autoColumns, 'rolePlateforme', 'sessionStatus', 'actions', 'checkbox'];
  notifParams: NotifParams = new NotifParams();
  user = jwt_decode(localStorage.getItem(CURRENT_USER_KEY));//TODO review this to use the Profile object from AuthenticationService
  hiddenTrash: boolean = true;
  polling;
  isAdmin = false;
  stompClient;
  serverUrl = `${host.API_ENDPOINT}session`;
  labelsPath: string = "pages.sessions.labels";
  dateFormat: string = "dd/MM/yyyy HH:mm:ss";
  metadata = reflectComponentType(ListSessionsComponent);

  constructor(private sessionService: SessionService,
              private router: Router,
              private datePipe: DatePipe,
              private modalService: PlatineModalService,
              private profileService: ProfileService,
              private userService: UsersService,
              private filterListService: FilterListService,
              private translateService: TranslateService,
              protected contextStateService: ContextStateService) {
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
    this.contextStateService.sessionsRecycleBin = !this.hiddenTrash;
    if (this.stompClient?.connected) {
      this.stompClient.disconnect();
    }
    if(this.dataSource.filteredData&&this.dataSource.sort){
      let sortedData = this.dataSource.sortData(this.dataSource.filteredData, this.dataSource.sort);
      let onlyDataWithDetails = sortedData.filter((session) =>
        ((session.application.role === 'SERVER') && (session.sessionStatus === 'FINISHED')) ||
        ((session.application.role === 'SERVER') && (session.sessionStatus === 'PENDING')) ||
        (session.application.role === 'CLIENT' && session.sessionStatus === 'PENDING') ||
        (session.application.role === 'CLIENT' && session.sessionStatus === 'FINISHED')
      );
      this.contextStateService.filteredSessionList = onlyDataWithDetails;
    }
  }

  ngOnInit() {
    this.hiddenTrash = !this.contextStateService.sessionsRecycleBin;
    
    this.dataSource.sortingDataAccessor = this.accessDataForSorting.bind(this);
   
    if (this.profileService.getProfileFromToken() != "user") {
      this.isAdmin = true;
    }
    if (this.hiddenTrash) {
      this.sessionService.getSessionsTestList().subscribe(session => {
        this.dataSource.data = session;
        this.restoreContext();
        setTimeout(() => this.contextStateService.refreshPaginator(this.dataSource));
      });
    } else {
      this.hiddenTrash = false;
      this.sessionService.getSessionsDisabled().subscribe(session => {
        this.dataSource.data = session;
        this.restoreContext();
        setTimeout(() => this.contextStateService.refreshPaginator(this.dataSource));
      });
    }
    this.ws();
  }
  
  private accessDataForSorting(data: SessionList, sortHeaderId: string): string {
    switch(sortHeaderId){
        case 'sessionStatus':
          return this.translateService.instant(this.labelsPath + '.statuses.' + data.sessionStatus);
        case 'rolePlateforme':
          return this.translateService.instant(this.labelsPath + '.roles.' + data.simulatedRole);
        case 'applicationName':
          return data.application.name.toLocaleLowerCase();
        case 'user':
          return (data.application.user.name + data.application.user.forename).toLocaleLowerCase();
        default:
          if (typeof data[sortHeaderId] === 'string') {
            return data[sortHeaderId].toLocaleLowerCase();
          }
          return data[sortHeaderId]
      }
  }
  
  private restoreContext(){
    this.contextStateService.restoreContext(
      this.dataSource,
      this.sort,
      this.paginator,
      this.metadata.selector
    )
    if(!this.dataSource?.sort.active){
      this.performDefaultSort();
    }
  }

  ngAfterViewInit() {
    this.dataSource.filterPredicate = (data: any, filter: string) => {
      return (data.application?.user?.name + ' ' + data.application?.user?.forename)?.toLowerCase().includes(filter) ||
        (this.datePipe.transform(data.creationDate, this.dateFormat))?.toLowerCase().includes(filter) ||
        data.application?.name?.toLowerCase().includes(filter) ||
        data.description?.toLowerCase().includes(filter) ||
        this.translateService.instant(this.labelsPath + '.roles.' + data.simulatedRole)?.toLowerCase().includes(filter) ||
        this.translateService.instant(this.labelsPath + '.statuses.' + data.sessionStatus)?.toLowerCase().includes(filter)
    };
  }

  loadSession() {
    this.sessionService.getSessionsTestList().subscribe(session => {
      this.dataSource.data = session;
      setTimeout(() => this.contextStateService.refreshPaginator(this.dataSource));
    });
  }

  ws() {
    let ws = new SockJS(this.serverUrl);
    this.stompClient = Stomp.over(ws);
    let that = this;
    this.stompClient.connect({}, function (frame) {
      that.stompClient.subscribe("/topic/sessions", (message) => {

        if (message.body) {
          that.loadSession();
        }
      });
    });

  }


  deleteSession() {

    if (this.selection.selected.length > 0) {
      let deleteSession = this.filterListService.filterListForDelete(this.selection.selected, this.dataSource.data);
      const confirmResult = this.modalService.openConfirm({
        modalHeader : MODAL_CONSTANT.MODAL_HEADER_DELETE_SESSION,
        modalContent : `Êtes-vous sûr de vouloir supprimer les sessions sélectionnées (${deleteSession.length}) ?`
      });
      
      confirmResult.then((res) => {
        if (res === 'confirm') {

          for (let session of deleteSession) {
            this.sessionService.delete(session.id).subscribe();
            this.dataSource.data = this.dataSource.data.filter((value, key) => {
              return value.id != session.id;
            });
            this.selection.clear();
            setTimeout(() => this.contextStateService.refreshPaginator(this.dataSource));
          }
        }
      });
    }
  }

  suppressionSession() {

    if (this.selection.selected.length > 0) {
      let supressSession = this.filterListService.filterListForDelete(this.selection.selected, this.dataSource.data);
      const confirmResult = this.modalService.openConfirm({
        modalHeader: MODAL_CONSTANT.MODAL_HEADER_DELETE_SESSION,
        modalContent : `Êtes-vous sûr de vouloir supprimer définitivement (${supressSession.length}) sessions de la base de données ?`
      });
      confirmResult.then((res) => {
        if (res === 'confirm') {

          for (let session of supressSession) {
            this.sessionService.suppress(session.id).subscribe();
            this.dataSource.data = this.dataSource.data.filter((value, key) => {
              return value.id != session.id;
            });
            this.selection.clear();
            setTimeout(() => this.contextStateService.refreshPaginator(this.dataSource));
          }
        }
      });
    }
  }

  restoreSession() {

    if (this.selection.selected.length > 0) {
      let restoreSession = this.filterListService.filterListForDelete(this.selection.selected, this.dataSource.data);
      for (let session of restoreSession) {
        this.sessionService.restoreSession(session).subscribe();
        this.dataSource.data = this.dataSource.data.filter((value, key) => {
          return value.id != session.id;
        });
        this.selection.clear();
        setTimeout(() => this.contextStateService.refreshPaginator(this.dataSource));
      }
    }
  }

  getSessionsRecycleBin() {
    this.hiddenTrash = false;
    this.sessionService.getSessionsDisabled().subscribe({
      next:  session => {
        this.dataSource.data = session;
        this.selection.clear();
        setTimeout(() => this.contextStateService.refreshPaginator(this.dataSource));
      },
      error: err => {
        this.modalService.openErrorModal(
        {
          err: err,
          modalHeader: "Erreur d'affiche de la corbeille des sessions."
        }
        );
      }
    });
  }

  getSessionsEnabled() {
    this.hiddenTrash = true;
    this.sessionService.getSessionsTestList().subscribe(session => {
      this.dataSource.data = session;
      this.selection.clear();
      setTimeout(() => this.contextStateService.refreshPaginator(this.dataSource));
    });
  }

  createSession(): void {
    this.router.navigate(['/pages/sessions/session']);
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
    if (numRows > 0) {
      return numSelected === numRows;
    } else {
      return false;
    }
  }

  applyFilter() {
    this.dataSource.filter = this.contextStateService.filters.get(this.metadata.selector)?.trim().toLowerCase();
  }

  executerSession(row) {
    this.sessionService.executeSession(row.id).subscribe(res => {
      console.info(`Successful session launch for ${row.id}`);
    }, err => {
      //FIXME ... *sigh* https://tickets.forge.ans.henix.fr/redmine/issues/58745
      console.debug(`SNAFU!!! Who TF had that wonderfull idea of a void get method ? ${err}`);
    });
  }

  details(row) {
    if (row.application.role === 'CLIENT') {
      this.router.navigate(['pages/sessions/sessionServeur'], {queryParams: {id: row.id}});
    } else {
      this.router.navigate(['/pages/sessions/details'], {
        queryParams: {
          id: row.id, detail: 'ok',
          status: row.sessionStatus
        }
      });
    }
  }

  edit(row) {
    this.router.navigate(['/pages/sessions/session'], {queryParams: {idSession: row.id}});
  }

  duplicate(row) {
    this.router.navigate(['/pages/sessions/session'], {queryParams: {idSession: row.id, isDuplicate: true}});
  }
  
  private performDefaultSort(){
    const sortedData = this.dataSource.data.slice();
    sortedData.sort(
      (a: SessionList,b: SessionList) => {
        if(!a?.creationDate || !b?.creationDate){
          return (a?.creationDate)? 1:-1;
        }else{
          
          return b.creationDate.toUTCString().localeCompare(a.creationDate.toUTCString(),'en',{numeric:true});
        }
      }
    );
    this.dataSource.data = sortedData;
  }
}
