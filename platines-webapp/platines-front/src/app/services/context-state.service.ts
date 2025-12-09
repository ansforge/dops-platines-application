///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {EventEmitter, Injectable} from '@angular/core';
import {MatTableDataSource} from "@angular/material/table";
import {MatSort, SortDirection} from "@angular/material/sort";
import {MatPaginator, PageEvent} from "@angular/material/paginator";
import {ProjectTreeNode} from "../pages/projet/component/projetDetail/project-tree/project-tree.model";

@Injectable({
  providedIn: 'root'
})
export class ContextStateService {

  public pageIndexes = new Map<string, number>();
  public scrollPositions = new Map<string, number>();
  public filters = new Map<string, string>();
  public activeSort = new Map<string, string>();
  public sortDirection = new Map<string, SortDirection>();

  public sessionsRecycleBin = false;

  public systemsFamily: any = "null";

  public versionsFamily: string = "null";

  public projectsSystem: string = "null";

  public compatibilityFamily = "";
  public compatibilitySystem = "";
  public compatibilityRole = "CLIENT";

  // Global page size of every table
  pageSize: number = 20;

  public filteredProjectList: any = [];
  public filteredSessionList: any = [];
  projectsSelectedNodes: ProjectTreeNode[] = [];

  constructor() { }

  public persistContext(pageIndex:number, pageSize:number, activeSort:string, sortDirection:SortDirection, scrollPosition:number, selector:string):void {
    this.pageIndexes.set(selector, pageIndex);
    this.scrollPositions.set(selector, scrollPosition);
    this.activeSort.set(selector, activeSort);
    this.sortDirection.set(selector, sortDirection);
    this.pageSize = pageSize;
  }

  public restoreContext(dataSource:MatTableDataSource<any>, sort:MatSort, paginator:MatPaginator, selector:string):void{
    dataSource.filter = this.filters.get(selector);
    this.restoreSort(dataSource, sort, selector);
    this.restorePaginationPosition(dataSource, paginator, selector);
  }

  public restoreSort(dataSource:MatTableDataSource<any>, sort:MatSort, selector: string):void {
    dataSource.sort = sort;
    sort.active = this.activeSort?.get(selector);
    sort.direction = this.sortDirection?.get(selector);
    sort.sortChange.emit(sort);
  }

  public restorePaginationPosition(dataSource:MatTableDataSource<any>, paginator:MatPaginator, pageName:string):void{
    dataSource.paginator = paginator;
    paginator.pageSize = this.pageSize;
    paginator.pageIndex = this.pageIndexes.get(pageName);
    paginator.page.next({
      pageIndex: this.pageIndexes.get(pageName),
      pageSize: paginator.pageSize,
      length: paginator.length
    })
    setTimeout(() => {
      window.scrollTo(0, this.scrollPositions.get(pageName));
    });
  }

  refreshPaginator(dataSource: MatTableDataSource<any>){
    (dataSource.paginator.page as EventEmitter<PageEvent>).emit({
      length: dataSource.paginator.length,
      pageIndex: dataSource.paginator.pageIndex,
      pageSize: dataSource.paginator.pageSize
    } as PageEvent);
  }

}
