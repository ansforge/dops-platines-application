///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import { Component, Input, OnInit } from '@angular/core';
import { SessionResultTreeNode } from '../session-tree.model';
import { Observable } from 'rxjs';
import { SessionDetail } from '../../../../entity/sessiondetail';
import { ProjectLibraryService } from '../../../../../services/projectlibrary.service';
import { SessionService } from '../../../../../services/session.service';
import * as FileSaver from 'file-saver';

@Component({
  selector: 'app-cadre-general-element-session',
  templateUrl: './cadre-general-element-session.component.html',
  styleUrls: ['./cadre-general-element-session.component.scss']
})
export class CadreGeneralElementSessionComponent implements OnInit{

  @Input() sessionDetails: Observable<SessionDetail>;
  @Input() nodeSelection: Observable<SessionResultTreeNode>;

  session: SessionDetail;
  currentNode: SessionResultTreeNode;
  nbStep: number;
  successStep: number;
  caseRatios: CaseRatios=null;
  files: File[]=[];
  
  
  constructor(
    private projectService: ProjectLibraryService,
    private sessionService: SessionService
  ){}

  ngOnInit(): void {
    this.sessionDetails.subscribe(
      (result: SessionDetail) => this.whenDataLoaded(result)
    );
    this.nodeSelection.subscribe(
      (selectedNode: SessionResultTreeNode) => this.currentNode=selectedNode
    );
  }

  whenDataLoaded(sessionData: SessionDetail){
    this.session=sessionData
    this.nbStep=0;
    this.successStep=0;

    if (sessionData.projectResults[0].idProject !== undefined) {
      this.projectService.getRelatedFiles(sessionData.projectResults[0].idProject).subscribe({
        next: (res: File[]) => {
          this.files = res;
        }, 
        error: (err) => {
          /* TODO (cf redmine 58653) : écrire ce handler d'erreur */
        }
      });
    }

    sessionData.projectResults.forEach(project => {
      
      let showCritLvl: boolean;
      const failRate = {critical: 0, major: 0, minor: 0};
      const critCount = {critical: 0, major: 0, minor: 0};
      

      if (project.resultStatus !== 'PENDING') {

        showCritLvl = project.testSuites.some(testSuite => testSuite.testCases.some(c => c.criticality != null));

        project.testSuites.forEach(testSuite => {

          failRate.critical = failRate.critical + testSuite.testCases.filter(c => c.resultStatus === 'FAILURE' && c.criticality === '2').length;
          failRate.major = failRate.major + testSuite.testCases.filter(c => c.resultStatus === 'FAILURE' && c.criticality === '1').length;
          failRate.minor = failRate.minor + testSuite.testCases.filter(c => c.resultStatus === 'FAILURE' && c.criticality === '0').length;

          critCount.critical = critCount.critical + testSuite.testCases.filter(c => c.criticality === '2').length;
          critCount.major = critCount.major + testSuite.testCases.filter(c => c.criticality === '1').length;
          critCount.minor = critCount.minor + testSuite.testCases.filter(c => c.criticality === '0').length;

          testSuite.testCases.forEach(testCase => {
            this.nbStep = this.nbStep + testCase.testSteps.length;
            this.successStep = this.successStep + testCase.testSteps.filter(step => step.resultStatus === 'SUCCESS').length;
          })
        })
      }

      if (showCritLvl) {
        const caseSuccessRate = Number(
          Math.max(
            0,
            100 - 100 * failRate.critical - 50 * failRate.major - 25 * failRate.minor
          )
        ).toFixed(0);

        this.caseRatios = {
          successRate: caseSuccessRate,
          criticalFailRate: Number(failRate.critical / critCount.critical * 100),
          majorFailRate:    Number(failRate.major / critCount.major * 100),
          minorFailRate: Number(failRate.minor / critCount.minor * 100)
        };
      }
    })
  }

  public get stepSuccessRate(){
    if (this.nbStep !== 0) {
      return Number(this.successStep / this.nbStep * 100).toFixed(0).toString();
    } else{
      return '-';
    }
  }

  public get dateExecution(): string{
    return this.localDate(this.session?.executionDate);
  }

  public get dateCreation(): string{
    return this.localDate(this.session?.creationDate);
  }

  private localDate(date: Date){
    if(date){
      const tsBugsDate: Date=new Date(date);
      const dateCreationUTC = Date.UTC(
        tsBugsDate.getFullYear(), 
        tsBugsDate.getMonth(),
        tsBugsDate.getDate(),
        tsBugsDate.getHours(),
        tsBugsDate.getMinutes(),
        tsBugsDate.getSeconds()
      );
      return new Date(dateCreationUTC).toLocaleString();
    }else{
      return '';
    }
    
  }

  public get dureeExecution(): Number {
    return Math.floor(Number.parseFloat(this.session.executionDuration.toString()) / 1000)
  }

  loadFile(event, file) {
    this.projectService.getFileById(file.id).subscribe(res => {
      FileSaver.saveAs(res, file.fileName);
    }, err => {
      /* TODO (cf redmine 58653) : écrire ce handler d'erreur */      
    });
  }

  downloadZip() {
    this.sessionService.getZipFile(this.session.id).subscribe({
      next: (res) => {
        const nomZip2 = "session_" + this.session.uuid + ".zip";
        FileSaver.saveAs(res, nomZip2);
      },
      error: err => {
        /* TODO (cf redmine 58653) : écrire ce handler d'erreur */
      }
    });
  }

  /** TODO : adapter, supprimer l'excès. Code historique de détection des fichiers.
   * 
  if (event.children === undefined && event.data.id !== undefined) {
    this.sessionService.getStepLogs(event.node.data.id).subscribe(res => {
      this.stepLog = res;
    }, err => {
      //this._service.error(ERROR_CONSTANT.ERROR_MESSAGE_TITLE, ERROR_CONSTANT.ERROR_MESSAGE_GET_LOGS, this.notifParams.notif)
    });
  } else {
    this.stepLog = null;
  }
  if (event.node.data.idProject !== undefined) {
    this.projectService.getRelatedFiles(event.node.data.idProject).subscribe(res => {
      this.files = res;
    }, err => {
      //this._service.error(ERROR_CONSTANT.ERROR_MESSAGE_TITLE, ERROR_CONSTANT.ERROR_MESSAGE_ALL_RELATED_FILES, this.notifParams.notif);
    });
  } else {
    this.files = [];
  }
  *
  */
}

export class CaseRatios{
  successRate: string;
  criticalFailRate?: Number;
  majorFailRate?: Number;
  minorFailRate?: Number;
}
