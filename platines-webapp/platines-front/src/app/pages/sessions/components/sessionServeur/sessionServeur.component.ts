///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {Component, EventEmitter, OnDestroy, OnInit} from '@angular/core';
import {UntypedFormBuilder} from '@angular/forms';
import {host} from '../../../../../environments/environment';
import {ActivatedRoute, Router} from '@angular/router';
import {SessionService} from '../../../../services/session.service';
import {SessionDetail} from '../../../entity/sessiondetail';
import * as Stomp from 'stompjs';
import * as SockJS from 'sockjs-client';
import * as FileSaver from 'file-saver';
import {ProjectLibraryService} from '../../../../services/projectlibrary.service';
import * as moment from 'moment';
import {MatTreeNestedDataSource} from "@angular/material/tree";
import {NestedTreeControl} from "@angular/cdk/tree";
import {SessionResultTreeNode} from "../detailSession/session-tree.model";
import {ContextStateService} from "../../../../services/context-state.service";
import { CURRENT_USER_KEY } from '../../../../services/authentication.service';
import { PlatineModalService } from "../../../ui/components/modal/platine-modal.service";

@Component({
  selector: 'sessionServeur',
  templateUrl: './sessionServeur.html',
  styleUrls: ['./sessionServeur.scss'],
})
export class SessionServeurComponent implements OnInit, OnDestroy {
  sessionSrc: EventEmitter<SessionDetail> = new EventEmitter();
  currentNodeObs: EventEmitter<SessionResultTreeNode> = new EventEmitter();

  id;
  treeDataSource: MatTreeNestedDataSource<any> = new MatTreeNestedDataSource<any>();
  treeControl: NestedTreeControl<any> = new NestedTreeControl<any>((node) => {
    if (node.children?.length > 0) return node.children;
  });

  nodes;
  user = JSON.parse(localStorage.getItem(CURRENT_USER_KEY));//TODO review this to use the Profile object from AuthenticationService
  session = new SessionDetail();
  description;
  sizeLogs: number = 0;
  sizeLogsErr: number = 0;
  statutSession;
  dateFinale;
  startDate;
  polling;
  logsOut: Array<String> = new Array<String>();
  logsErr: Array<String> = new Array<String>();
  logsConn: Array<String> = new Array<String>();
  bilan = false;
  plan = false;
  history = false;
  request = '';
  response = '';
  nbCase:  number;
  nbCasesSuccess: number;
  duration;
  creationDate;
  result;
  stepLog;
  url;
  application;
  resourcePath;
  files = [];
  activeNode: any;
  currentPosition: number = null;
  private stompClient;
  private serverUrl = `${host.API_ENDPOINT}session`;
  private routeSub: any;

  constructor(public fb: UntypedFormBuilder,
              protected sessionService: SessionService,
              private projectService: ProjectLibraryService,
              private route: ActivatedRoute,
              private modalService: PlatineModalService,
              private router: Router,
              protected contextStateService: ContextStateService) {

  }

  ngOnDestroy() {
    clearInterval(this.polling);
    if (this.stompClient?.connected) {
      this.stompClient.disconnect();
    }
    this.routeSub.unsubscribe();
  }

  ngOnInit() {

    this.plan = true;
    this.routeSub = this.route
      .queryParams
      .subscribe(params => {
        if (this.contextStateService.filteredSessionList.length > 0) {
          this.currentPosition = this.contextStateService.filteredSessionList.map(function (e) {
            return e.id
          }).indexOf(Number(this.route.snapshot.queryParams['id']));
        }
        if (params['id'] !== undefined) {
          this.id = params['id'];
          this.loadSession();
          this.ws();
        }
      }, (err) => {
        this.modalService.openErrorModal({
          modalHeader:"Erreur de chargement des paramètres.",
          err:err
        });
      });
  }

  loadSession() {
    this.sessionService.getSessionById(this.id).subscribe(res => {
      this.application = res.application;
    });
    this.sessionService.getSessionDetails(this.id).subscribe(result => {
      this.session = result;
      this.sessionSrc.emit(this.session);
      this.loadTree();
      this.sessionService.getROperationBySession(this.session.id).subscribe(res => {
        res.forEach(element => {
          const dateElement = new Date(element.operationDate);
          const dateUTC2 = Date.UTC(dateElement.getFullYear(), dateElement.getMonth(),
            dateElement.getDate(), dateElement.getHours(), dateElement.getMinutes(), dateElement.getSeconds());
          element.operationDate = new Date(dateUTC2).toLocaleString();
          element.isOperation = true;
        })
        this.result = res;

      });
      const duree = this.session?.sessionDuration?.duration / 60;
      if (duree <= 1) {
        this.duration = duree + ' heure';
      } else {
        this.duration = duree + ' heures';
      }

      const rProjet = this.session.projectResults[0];
      if (rProjet.idProject !== undefined) {
        this.projectService.getRelatedFiles(rProjet.idProject).subscribe(res => {
          this.files = res;
        });
        this.description = rProjet.description;
      }

      const date = new Date(this.session.creationDate);
      const dateUTC = Date.UTC(date.getFullYear(), date.getMonth(),
        date.getDate(), date.getHours(), date.getMinutes(), date.getSeconds());
      this.creationDate = new Date(dateUTC).toLocaleString();
      this.startDate = this.convertDate(new Date(this.session.executionDate));
      this.calculateDateFinale();
      this.url = this.session.url;
      this.resourcePath = this.session.resourcePath;
    });
  }

  loadTree() {
    this.nodes = [];
    this.description = "Sélectionner un élément de l'arborescence (thème, webservice ou version) pour gérer les ressources correspondantes";
    this.nbCase = 0;
    this.nbCasesSuccess = 0;
    this.session.projectResults.forEach(projectResult => {
      const projectResultTree = {
        description: projectResult.description,
        id: projectResult.id,
        idProject: projectResult.idProject,
        name: projectResult.name,
        resultStatus: projectResult.resultStatus,
        children: []
      }
      projectResult.testSuites.forEach(testSuite => {
        const testSuiteTree = {
          description: testSuite.description,
          id: testSuite.id,
          name: testSuite.name,
          resultStatus: testSuite.resultStatus,
          children: []
        }
        testSuite.testCases.forEach(testCase => {
          const testCaseTree = {
            criticality: testCase.criticality,
            description: testCase.description,
            id: testCase.id,
            name: testCase.name,
            rOperationDto: testCase.rOperationDto,
            resultStatus: testCase.resultStatus,
            testSteps: testCase.testSteps,
            children: []
          }
          if(testCase.rOperationDto){
            testCase.rOperationDto.forEach(operation => {
                const date = new Date(operation.operationDate);
                const dateUTC = Date.UTC(date.getFullYear(), date.getMonth(), date.getDate(),
                  date.getHours(), date.getMinutes(), date.getSeconds());
                const rOperationTree = {
                  id: operation.id,
                  name: new Date(dateUTC).toLocaleString()
                }
              testCaseTree.children.push(rOperationTree);
            })
          }
          this.nbCase = this.nbCase + 1;
          if (testCase.resultStatus === 'SUCCESS') {
            this.nbCasesSuccess = this.nbCasesSuccess + 1;
          }
          testSuiteTree.children.push(testCaseTree)
        })
        testSuiteTree.children = testSuiteTree.children.sort((a, b) => {
          if (a.name < b.name) return -1;
          if (a.name > b.name) return 1;
          return 0;
        });
        projectResultTree.children.push(testSuiteTree);
      });
      projectResultTree.children = projectResultTree.children.sort((a, b) => {
        if (a.name < b.name) return -1;
        if (a.name > b.name) return 1;
        return 0;
      });
      this.nodes.push(projectResultTree);
    });
    this.treeDataSource.data = this.nodes;
    this.treeControl.dataNodes = this.nodes;
    this.nodes = this.nodes.sort((a, b) => {
      if (a.name < b.name) return -1;
      if (a.name > b.name) return 1;
      return 0;
    });
  }

  onEvent(event) {
    this.currentNodeObs.emit(event);
    this.activeNode = event;
    this.description = event.payload.description;
    if (event.payload.isOperation) {
      this.sessionService.getStepLogsServer(event.payload.id).subscribe(res => {
        this.stepLog = res;
      }, (err) => {
        this.modalService.openErrorModal({
          modalHeader:"Erreur à la sélection du noeud",
          err:err
        });
      });
    } else {
      this.stepLog = null;
    }
  }

  ws() {
    this.sessionService.getSessionDetails(this.id).subscribe(result => {
      const ws = new SockJS(this.serverUrl);
      this.stompClient = Stomp.over(ws);
      const that = this;
      const uuid = result.uuid;
      this.stompClient.connect({}, function (frame) {
        that.stompClient.subscribe('/topic/mock.' + uuid + '.stdout', (message) => {
          if (message.body) {
            let strings: string[]
            strings = JSON.parse(message.body);
            strings.forEach(function (value) {
              that.logsOut.push(value);
            })
          }
        });
        that.stompClient.subscribe('/topic/connection.' + uuid + '.stderr', (connectionLogs) => {
          if (connectionLogs.body) {
            let strings: string[]
            strings = JSON.parse(connectionLogs.body);
            strings.forEach(function (value) {
              that.logsConn.push(value);
            })
          }
        });
        that.stompClient.subscribe('/topic/mock.' + uuid + '.stderr', (logErr) => {
          if (logErr.body) {
            let strings: string[]
            strings = JSON.parse(logErr.body);
            strings.forEach(function (value) {
              that.logsErr.push(value);
            })
          }
        });
        that.stompClient.subscribe('/topic/sessions.' + uuid, (message) => {
          if (message.body) {
            that.loadSession();
          }
        })

      });
    });
  }

  convertDate(date): string {
    const dateUTC = Date.UTC(date.getFullYear(), date.getMonth(), date.getDate(), date.getHours(),
      date.getMinutes(), date.getSeconds());
    return new Date(dateUTC).toLocaleString();
  }

  calculateDateFinale() {
    if (this.session.executionDate !== null) {
      const duree = this.session?.sessionDuration?.duration;
      let date = new Date(this.session.executionDate);
      date = new Date(date.setHours(date.getHours() + (duree?.valueOf() / 60)));
      this.dateFinale = this.convertDate(date);
    }
  }

  replaceDateInString(str): string {
    const tim = str.substring(
      str.lastIndexOf('[') + 1,
      str.lastIndexOf(']'),
    );

    const utcMoment = moment(tim, 'DD/MMM/YYYY:HH:mm:ss Z');

    if (utcMoment.isValid()) {
      const utcDate = utcMoment.toDate();
      const localDate = moment(utcDate).local().format('YYYY-MM-DD HH:mm:ss');
      return '[' + localDate.toString() + ']' + str.substring(str.lastIndexOf(']') + 1);
    } else {
      return null
    }
  }

  getPlan(event) {
    if (event === 1) {
      this.bilan = false;
      this.plan = false;
      this.history = true;
    } else {
      this.bilan = false;
      this.plan = true;
      this.history = false;
    }

  }

  downloadZip() {
    this.sessionService.getZipFile(this.id).subscribe(res => {
      const nomZip = 'session_' + this.session.uuid + '.zip';
      FileSaver.saveAs(res, nomZip);
    });
  }

  loadFile(event, file) {
    this.projectService.getFileById(file.id).subscribe(res => {
      FileSaver.saveAs(res, file.fileName);
    });
  }

  hasNestedChild(index: number, node: any) {
    return (
      node?.children?.length > 0
    );
  }

  back() {
    this.router.navigate(["/pages/sessions/listSessions"]);
  }
}
