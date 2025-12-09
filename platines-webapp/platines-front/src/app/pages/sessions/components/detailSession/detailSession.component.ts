///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {Component, EventEmitter, OnChanges, OnDestroy, OnInit} from '@angular/core';
import {host} from '../../../../../environments/environment';
import {ActivatedRoute, Router} from '@angular/router';
import {SessionService} from '../../../../services/session.service';
import {SessionDetail} from '../../../entity/sessiondetail';
import {NotifParams} from '../../../entity/notifparams';
import {StepLog} from '../../../entity/steplog';
import * as Stomp from 'stompjs';
import * as SockJS from 'sockjs-client';
import {SessionResultTreeNode} from './session-tree.model';
import {ContextStateService} from "../../../../services/context-state.service";
import { CURRENT_USER_KEY } from '../../../../services/authentication.service';

@Component({
  selector: 'detailSession',
  templateUrl: './detailSession.html',
  styleUrls: ['./detailSession.scss'],
})
export class DetailSessionComponent implements OnDestroy, OnInit, OnChanges {
  sessionSrc: EventEmitter<SessionDetail> = new EventEmitter();
  currentNodeObs: EventEmitter<SessionResultTreeNode> = new EventEmitter();

  /** websocket (ws) à refactorer */
  session: SessionDetail;

  /** TODO : Ci-dessous : code mort et/ou mal packagé à refactorer */
  notifParams: NotifParams = new NotifParams();
  stepLog: StepLog = null;
  id: number;

  btnExecute = false;
  errorConsole = false;
  errorText = null;
  nomProjet: String;
  user = JSON.parse(localStorage.getItem(CURRENT_USER_KEY));//TODO review this to use the Profile object from AuthenticationService
  request = null;
  response = null;

  private stompClient;
  private serverUrl = `${host.API_ENDPOINT}session`;
  currentPosition: number = null;
  protected activeNode: SessionResultTreeNode;

  constructor(private router: Router,
              private route: ActivatedRoute,
              protected sessionService: SessionService,
              protected contextStateService: ContextStateService) {

  }

  ngOnChanges() {

  }

  ngOnInit() {

  }

  ngAfterViewInit() {
    this.route
      .queryParams
      .subscribe(params => {
        if (this.contextStateService.filteredSessionList.length > 0) {
          this.currentPosition = this.contextStateService.filteredSessionList.map(function (e) {
            return e.id
          }).indexOf(Number(this.route.snapshot.queryParams['id']));
        }
        if (params['id'] !== undefined) {
          this.id = params['id'];
          this.sessionService.getSessionDetails(this.id).subscribe(
            {
              next: (result: SessionDetail) => {

                this.session = result; // TODO : maintenu pour ws(), sujet à révision

                this.sessionSrc.emit(result);

                // à vérifier : est-ce qu'on peut se trouver sur cet écran avec une session non-terminèe ?
                if (result.sessionStatus === 'PENDING') {
                  this.ws();
                }
              },
              error: (err: any) => {
                /* TODO (cf redmine 58653) : écrire ce handler d'erreur */
              }
            });
        } else {
          //WTF is a session detail without session id supposed to mean ?
        }
      });
  }

  ws() {
    let ws = new SockJS(this.serverUrl);
    this.stompClient = Stomp.over(ws);
    let that = this;
    this.stompClient.connect({}, function (frame) {
      that.stompClient.subscribe("/topic/sessions." + that.id, (message) => {

        if (message.body) {
          that.sessionService.getSessionDetails(that.id).subscribe(() => {
            that.displaySession(that);
          })
        }
      });
      that.session.projectResults.forEach(rprojet => {
        that.stompClient.subscribe("/topic/rproject." + rprojet.id, (message) => {
          if (message.body) {
            that.displaySession(that);
          }
        });
      })


    });
  }

  displaySession(that) {
    that.sessionService.getSessionDetails(that.id).subscribe(result => {
      that.session = result;
      let date = new Date(that.session.executionDate);
      that.setExecutionDate(date);
      const rProjet = that.session.projectResults[0];
      that.description = rProjet.description;
      that.proprietesRProjet = rProjet.projectProperties;
      that.tree.treeModel.nodes = that.loadNodes();
      that.tree.treeModel.update();
      that.tree.treeModel.expandAll();
      if (rProjet.idProject !== undefined) {
        that.projectService.getRelatedFiles(rProjet.idProject).subscribe(res => {
          that.files = res;
        }, err => {
          /* TODO (cf redmine 58653) : écrire ce handler d'erreur */
        });
      }
      if (that.session.executionDuration != null) {
        that.dureeExecution = Math.floor(Number.parseFloat(that.session.executionDuration.toString()) / 1000);
      }
    });
  }

  ngOnDestroy() {
    if (this.stompClient?.connected) {
      this.stompClient.disconnect();
    }

  }

  onNodeSelect(event: SessionResultTreeNode) {
    this.currentNodeObs.emit(event);
    this.activeNode = event;
    if (!(event.payload['testSuites'] || event.payload['testCases'] || event.payload['testSteps']))
      this.sessionService.getStepLogs(event.payload['id']).subscribe(result => {
        this.stepLog = result;
      });
    else
      this.stepLog = null;
  }

  mapCriticality(crit) {
    if (crit == "0") {
      return "MINEUR"
    } else if (crit == "1") {
      return "MAJEUR"
    } else if (crit == "2") {
      return "CRITIQUE"
    } else {
      return null
    }
  }

  downloadRequest() {
    this.downloadTxt(this.stepLog.request, "request.txt");
  }

  downloadResponse() {
    this.downloadTxt(this.stepLog.response, "response.txt");
  }

  downloadError() {
    this.downloadTxt(this.stepLog.error, "error.txt");
  }

  downloadTxt(text, filename) {
    var element = document.createElement('a');
    element.setAttribute('href', 'data:text/plain;charset=iso-8859-1,' + encodeURIComponent(text));
    element.setAttribute('download', filename);

    element.style.display = 'none';
    document.body.appendChild(element);

    element.click();

    document.body.removeChild(element);
  }

  back() {
    this.router.navigate(["/pages/sessions/listSessions"]);
  }
}
