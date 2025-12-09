///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {AfterViewInit, Component, EventEmitter, OnInit, reflectComponentType, ViewChild} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {ProjectLibraryService} from '../../../../services/projectlibrary.service';
import {Version} from '../../../entity/version';
import {NotifParams} from '../../../entity/notifparams';
import {ProfileService} from '../../../../services/profile.service';
import {FilterListService} from '../../../../services/filterlist.service';
import {MatSort} from "@angular/material/sort";
import {MatTableDataSource} from "@angular/material/table";
import {SelectionModel} from "@angular/cdk/collections";
import {ProjectList} from '../../../entity/projectlist';
import {PlatineModalService} from '../../../ui/components/modal/platine-modal.service';
import {MatPaginator} from "@angular/material/paginator";
import {TranslateService} from "@ngx-translate/core";
import {DatePipe} from "@angular/common";
import {ContextStateService} from "../../../../services/context-state.service";
import {ProjectTreeNode} from "../projetDetail/project-tree/project-tree.model";
import {Theme} from "../../../entity/theme";
import { BulkUpdateChoiceboxComponent } from './bulk-update-choicebox/bulk-update-choicebox.component';
import { BulkUpdateService } from '../bulkUpdateService.service';
import { CURRENT_USER_KEY } from '../../../../services/authentication.service';


@Component({
  selector: 'listProjects',
  templateUrl: './listProjets.html',
  styleUrls: ['./listProjets.scss'],

})
export class ListProjetsComponent implements OnInit, AfterViewInit {

  @ViewChild(MatPaginator) paginator: MatPaginator;
  @ViewChild(MatSort) sort: MatSort;

  dataSource: MatTableDataSource<ProjectList> = new MatTableDataSource();
  selection: SelectionModel<any> = new SelectionModel<any>(true, []);
  autoColumns: string[] = ['name'];
  allColumns: string[];

  userStock = JSON.parse(localStorage.getItem(CURRENT_USER_KEY));//TODO review this to use the Profile object from AuthenticationService
  isManager: boolean = false;
  versions: Version[];
  notifParams: NotifParams = new NotifParams();

  labelsPath: string = "pages.projects.labels";
  dateFormat: string = "dd/MM/yyyy HH:mm:ss";

  metadata = reflectComponentType(ListProjetsComponent);
  projectSrc: EventEmitter<Theme[]> = new EventEmitter();

  currentSubscription: any;
  @ViewChild("scrollableTreeContainer") scrollableTreeContainer;
  selectedNodes: ProjectTreeNode[] = [];
  loadingDataSource: boolean = false;
  scrolledTo: number = 0;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private modalService: PlatineModalService,
    private projectService: ProjectLibraryService,
    private bulkUpdateService: BulkUpdateService,
    private profileService: ProfileService,
    private filterListService: FilterListService,
    private translationService: TranslateService,
    private datePipe: DatePipe,
    protected contextStateService: ContextStateService
  ) {

    if (
      this.profileService.getProfileFromToken().toString() === 'admin' ||
      this.profileService.getProfileFromToken().toString() === 'manager'
    ) {
      this.isManager = true
    }
    this.buildAllColumns(this.isManager);
  }

  ngOnInit() {
    this.selectedNodes = this.contextStateService.projectsSelectedNodes;
    this.dataSource.filterPredicate = (data: any, filter: string) => {
      return data.name?.toLowerCase().includes(filter) ||
        data.description?.toLowerCase().includes(filter) ||
        (this.translationService.instant(this.labelsPath + ".roles." + data.role))?.toLowerCase().includes(filter) ||
        (this.datePipe.transform(data.dateUpload, this.dateFormat)).toLowerCase().includes(filter);
    }

    this.projectService.getFamiliesAsTree().subscribe(res => {
      this.projectSrc.emit(res);
    });

    this.projectService.getAllVersions().subscribe(res => {
      this.versions = this.projectService.sortVersion(res);
    });
    if (this.selectedNodes.length > 0) {
      this.loadingDataSource = true;
      this.currentSubscription = this.projectService.getProjectsByNodeArray(this.selectedNodes).subscribe(
        (res) => {
          this.dataSource.data = this.removeDuplicates(this.dataSource.data.concat(res.flat()));
          this.contextStateService.restoreContext(
            this.dataSource,
            this.sort,
            this.paginator,
            this.metadata.selector
          )
          setTimeout(() => this.contextStateService.refreshPaginator(this.dataSource));
          this.loadingDataSource = false;
        }, () => {
          this.loadingDataSource = false;
        }
      )
    }
  }

  ngOnDestroy() {
    this.contextStateService.scrollPositions.set('projectListTree', this.scrolledTo);
    this.contextStateService.projectsSelectedNodes = this.selectedNodes;
    this.contextStateService.persistContext(
      this.paginator.pageIndex,
      this.paginator.pageSize,
      this.sort.active,
      this.sort.direction,
      window.scrollY,
      this.metadata.selector
    )
    this.contextStateService.filteredProjectList = this.dataSource.sortData(this.dataSource.filteredData, this.dataSource.sort);
  }

  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

  getData() {
    this.projectService.getAllVersions().subscribe(res => {
      this.versions = this.projectService.sortVersion(res);
    });

    this.projectService.getAllProjects().subscribe(res => {
      this.dataSource.data = res;
      setTimeout(() => this.contextStateService.refreshPaginator(this.dataSource));
    });
  }

  addProject() {
    this.router.navigate(['/pages/projet/create']);
  }

  deleteProject() {
    let supressProjet = this.filterListService.filterListForDelete(this.selection.selected, this.dataSource.data);
    const modalResultObserver = this.modalService.openConfirm(
      {
        modalHeader: 'Suppression d\'un projet de test',
        modalContent: `Êtes-vous sûr de vouloir supprimer les tests selectionnés (${supressProjet.length}) ?`
      }
    )

    for (let i = supressProjet.length - 1; i >= 0; i--) {
      const obj = supressProjet[i];
      const obj2 = JSON.parse(JSON.stringify(supressProjet[i]));
      modalResultObserver.then((res) => {
        if (res === 'confirm') {
          this.projectService.deleteProject(obj2.id).subscribe(() => {
            this.dataSource.data = this.dataSource.data.filter(item => item !== obj);
            setTimeout(() => this.contextStateService.refreshPaginator(this.dataSource));
            this.selection.clear();
          }, err => {
            this.modalService.openErrorModal({
              modalHeader: 'Erreur',
              err: err
            })
          });
        }
      });
    }
  }

  downloadCertif(projectItem: ProjectList) {
    const idCertif: number = projectItem.testCertificate.id;
    this.projectService.downloadTestCertificate(idCertif);
  }

  downloadProject(projectItem: ProjectList) {
    const idProject: Number = projectItem.id;
    this.projectService.downloadProject(idProject);
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

  toggleExpandDescription(element) {
    element.expandedDescription = !element.expandedDescription;
  }

  onNodeSelect($event: { node: ProjectTreeNode, event: MouseEvent }) {
    if (this.currentSubscription) {
      this.currentSubscription.unsubscribe();
    }
    if ($event.event.ctrlKey) {
      this.toggleNodeSelection($event.node);
    } else {
      this.selectOnlyOneNode($event.node);
    }
  }

  toggleNodeSelection(node: ProjectTreeNode) {
    this.loadingDataSource = true;
    if (this.selectedNodes.includes(node)) {
      if (this.selectedNodes.length > 1) {
        this.deselectNode(node);
      }
    } else {
      this.selectNode(node);
    }
  }

  deselectNode(node: ProjectTreeNode) {
    this.selectedNodes = this.selectedNodes.filter(n => n !== node);
    this.refreshSelectionByArray();
  }

  selectNode(node: ProjectTreeNode) {
    this.selectedNodes.push(node);
    this.refreshSelectionByArray();
  }

  refreshSelectionByArray() {
    this.dataSource.data = [];
    this.currentSubscription = this.projectService.getProjectsByNodeArray(this.selectedNodes).subscribe(
      (res) => {
        this.dataSource.data = this.removeDuplicates(this.dataSource.data.concat(res.flat()));
        setTimeout(() => this.contextStateService.refreshPaginator(this.dataSource));
        this.loadingDataSource = false;
      }, () => {
        this.loadingDataSource = false;
      }
    )
  }

  selectOnlyOneNode(node: ProjectTreeNode) {
    this.dataSource.data = [];
    this.loadingDataSource = true;
    this.selectedNodes = [node];
    this.currentSubscription = this.projectService.getProjectsByNode(node).subscribe(
      res => {
        this.dataSource.data = this.removeDuplicates(res.flat());
        setTimeout(() => this.contextStateService.refreshPaginator(this.dataSource));
        this.loadingDataSource = false;
      }
    );
  }

  removeDuplicates(array: any[]): any[] {
    return array.filter((item, index, self) =>
        index === self.findIndex((t) => (
          t.id === item.id
        ))
    );
  }

  /**
   * La construction de la liste des colonnes est non-triviale (voir ci-dessous),
   * donc définission là dans cette méthode privée pour trouver facilement cette définition.
   *
   * - la liste doit contenir toutes les colonnes de this.autoColumns
   * - l'ordre voulu des colonnes mêle colonnes 'autoColumns' et colonnes additionelles.
   *
   * TODO : vérifier la pertinence de la notion 'autocolumn'
   */
  private buildAllColumns(isManager: boolean) {
    const collList = [...this.autoColumns];
    /* Insérer ces colonnes, dans cet ordre, à partir de la seconde position à gauche,
     * respecter l'ordre prescrit.
     */
    collList.splice(1, 0, 'dateUpload', 'role', 'description');

    /**
     * Seuls les administrateurs voient les bibliothèques de test privées.
     * Il n'est pas la peine de montre aux autres la colonne visibility.
     */
    if (isManager) {
      collList.push('visibility');
    }
    this.allColumns = collList.concat(['actions', 'checkbox']);
  }

  toggleExpandName(element) {
    element.expandedName = !element.expandedName;
  }

  onBulkMaj(): void{
    const services=[
      {
        provide: ProjectLibraryService, 
        useValue:  this.projectService
      },
      {
        provide: BulkUpdateService, 
        useValue:  this.bulkUpdateService
      }
    ];
    this.modalService.openComponentModal(BulkUpdateChoiceboxComponent,services);
  }
}
