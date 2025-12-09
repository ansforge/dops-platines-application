///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

/**
 * (c) Copyright 1998-2023, ANS. All rights reserved.
 */
import { HttpClient, HttpHeaders } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { BehaviorSubject, Observable } from "rxjs";
import {environment} from "../../../../environments/environment";
import { BulkUpdateReport } from "../../entity/bulk-update-report.model";

@Injectable()
export class BulkUpdateService {

  lastBulkReport: BehaviorSubject<BulkUpdateReport> = new BehaviorSubject(null);
  lastBulkArchive: BehaviorSubject<Blob> = new BehaviorSubject(null);

  constructor(private http:HttpClient){}
  
  bulkProjectDryRun(
  archiveData: Blob, themeId: number): Observable<BulkUpdateReport>{
    this.lastBulkArchive.next(archiveData);
    return this.bulkArchiveSubmit(archiveData,themeId,true);
  }
  
  bulkProjectUpdate(archiveData: Blob, themeId: number): Observable<BulkUpdateReport>{
    this.lastBulkArchive.next(null);
    return this.bulkArchiveSubmit(archiveData,themeId,false);
  }
  
  private bulkArchiveSubmit(archiveData: Blob, themeId: number, isDryRun:boolean):Observable<BulkUpdateReport>{
    const dryRunClause=isDryRun?"&dryRun=true":"";
    const headers=new HttpHeaders(
    {
      'Content-Type': 'application/zip'
    }
    );
    return this.http.post<BulkUpdateReport>(
      `${environment.API_ENDPOINT_SECURE}project/update/bulk?themeId=${themeId}${dryRunClause}`,
      archiveData,{headers: headers}
      );
  }
  
  setLastReport(report: BulkUpdateReport){
    this.lastBulkReport.next(report);
  }
    
  
  getLastReport(): Observable<BulkUpdateReport>{
    return this.lastBulkReport;
  }
  
  getLastArchive(): Observable<Blob>{
    return this.lastBulkArchive;
  }
}
