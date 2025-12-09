///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

/**
 * (c) Copyright 1998-2023, ANS. All rights reserved.
 */
import { Component, OnInit } from '@angular/core';
import { BulkReportLine, BulkUpdateReport } from "../../../entity/bulk-update-report.model";
import { ProjectLibraryService } from "../../../../services/projectlibrary.service";
import { MatTableDataSource } from '@angular/material/table';
import { BulkUpdateService } from '../bulkUpdateService.service';
import { ActivatedRoute, Router } from '@angular/router';
import { BehaviorSubject } from 'rxjs';
import { AuthenticationService } from '../../../../services/authentication.service';
import * as FileSaver from 'file-saver';

@Component({
  selector: 'app-bulk-update-report',
  templateUrl: './bulk-update-report.component.html',
  styleUrls: ['./bulk-update-report.component.scss']
})
export class BulkUpdateReportComponent implements OnInit{
  isDryRun: boolean;
  archiveId: string;
  archiveData: Blob;
  report: BulkUpdateReport;
  tableDatasource: MatTableDataSource<BulkReportLine> = new MatTableDataSource();
  allColumns: string[]=['filename','previousName','newName','lastUpdateDate','status','statusMessage'];
  
  constructor(
    private bulkUpdateService: BulkUpdateService,
    private activeRoute: ActivatedRoute,
    private router: Router,
    private authenticationService: AuthenticationService
  ){}
  
  ngOnInit(): void {
    this.activeRoute.queryParamMap.subscribe(
        params => {
          this.isDryRun = params.get('dryRun') === 'true'
          this.archiveId = params.get('archiveId');
          
          const svgState=localStorage.getItem('bulk.project.state');
          if(svgState){
              const state: StateForLocalStore=JSON.parse(svgState);
              if(this.archiveId===state.report.archiveId){
                  restoreArchiveData(state,blob => this.archiveData=blob);
                  this.report=state.report;
                  this.tableDatasource.data=this.report?.reportLines;
              }
          }
        }
    );
    this.bulkUpdateService.getLastReport().subscribe({
      next: report => {
        if(report){
            this.report=report
            this.tableDatasource.data=report?.reportLines;
            this.storeIfComplete();
        }
      }
    });
    this.bulkUpdateService.getLastArchive().subscribe({
      next: archiveData => {
          if(archiveData) {
              this.archiveData = archiveData;
              this.storeIfComplete();
          }
      }
    });
  }
  
  private storeIfComplete(){
      if(this.archiveData && this.report){
          const state=new StateForLocalStore(this.archiveData,this.report);
          state.waitForReady( 
              () => {
                const rep = {report: state.report,archiveDataArray: state.archiveDataArray, ready:true};
                localStorage.setItem('bulk.project.state',JSON.stringify(rep));
              }
          );
      }
  }
  
  onBulkConfirm(){
    this.bulkUpdateService.bulkProjectUpdate(this.archiveData,this.report.themeId).subscribe({
      next: report => {
        /*
         * We need this because the transition to dryRun won't trigger navigation, 
         * but we need to have dryRun and archiveId refreshed.
         */
        this.authenticationService.updateRelogFromUrl(`pages/projet/bulk-update-report?dryRun=false&archiveId=${report.archiveId}`);
        this.bulkUpdateService.setLastReport(report);
        this.router.navigate(['.'], {relativeTo: this.activeRoute, queryParams: {"archiveId":report.archiveId,"dryRun":"false"},queryParamsHandling: 'merge'});
      }
    });
  }
  
  onReportDownload(){
      const DATE_FORMAT:  RegExp=/\.[0-9]{3}\+[0-9]{2}:[0-9]{2}/g;
      const reportHeader: string="Fichier projet;Statut;Dernière m-à-j;Nouveau nom;Ancien nom;Explication"
      
      const reportData: string=this.report.reportLines.flatMap(
          (line: BulkReportLine) => line.filename+';'+line.status+';"'+line.lastUpdateDate?.toString().replace('T','\n').replace(DATE_FORMAT,'')+'";'+line.newName+';'+line.previousName+';"'+line.statusMessage+'"'
      ).join('\n');
      
      const reportContent: string=
          reportHeader+'\n'+
          reportData+'\n';
      
      const reportBlob: Blob=new Blob([reportContent],{type: "application/csv"});
      FileSaver.saveAs(reportBlob, 'bulk_'+this.report.archiveId+'.csv');
  }
}

class StateForLocalStore {
  public archiveDataArray: string;
  private _ready = false;
  private readySignal: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(this._ready);
  
  constructor(
    archiveDataBlob: Blob,
    public report: BulkUpdateReport
  ){
    var reader=new FileReader();
    reader.addEventListener("loadend"
        , ()=>{
            this.archiveDataArray=reader.result.toString();
            this._ready=true;
            this.readySignal.next(true);
        }
    );
    reader.readAsDataURL(archiveDataBlob);
  }
  
  get ready(): boolean{
    return this._ready;
  }
  
  waitForReady(action: () => void): void{
    this.readySignal.subscribe(
      value => {
        if(value){
          action.call(null);
        }
      }
    );
  }
}

function restoreArchiveData(state: StateForLocalStore,responseHandler: (Blob) => {}): void{
      fetch(state.archiveDataArray).then(
          reponse => {
              if(reponse.ok){
                  reponse.blob().then(
                      blob => responseHandler.call(null,blob)
                  );
              }else{
                  console.error(reponse.statusText);
              }
         }
      );
}