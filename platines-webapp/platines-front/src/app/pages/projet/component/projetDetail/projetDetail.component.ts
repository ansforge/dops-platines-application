///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {Component, EventEmitter, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {ProjectLibraryService} from '../../../../services/projectlibrary.service';
import {FileService} from '../../../../services/file.service';
import {Version} from '../../../entity/version';
import * as FileSaver from 'file-saver';
import {NotifParams} from '../../../entity/notifparams';
import {ProfileService} from '../../../../services/profile.service';
import {MatTableDataSource} from "@angular/material/table";
import {ProjectTreeNode} from './project-tree/project-tree.model';
import {ProjectDetails} from '../../../entity/projectDetail';
import {ContextStateService} from "../../../../services/context-state.service";
import { CURRENT_USER_KEY } from '../../../../services/authentication.service';

@Component({
  selector: 'projetDetail',
  templateUrl: './projetDetail.html',
  styleUrls: ['./projetDetail.scss'],
})
export class ProjetDetailComponent implements OnInit {

  projectSrc: EventEmitter<ProjectDetails> = new EventEmitter();
  projectName: string;
  versions: Version[];
  description: string;
  activeNode: ProjectTreeNode;

  // TODO : this component is grossly overweight, we should divide it !
  tableDataSource: MatTableDataSource<any> = new MatTableDataSource<any>();
  autoColumns: string[] = ['key', 'value', 'description'];
  allColumns: string[] = [...this.autoColumns];

  notifParams: NotifParams = new NotifParams();
  userStock = JSON.parse(localStorage.getItem(CURRENT_USER_KEY));//TODO review this to use the Profile object from AuthenticationService

  files = [];
  resources = [];

  labelsPath: string = "pages.projects.labels.project";
  currentPosition: number = null;

  constructor(
    private projectService: ProjectLibraryService,
    private fileService: FileService,
    private router: Router,
    private route: ActivatedRoute,
    protected contextStateService: ContextStateService
  ) {
  }

  ngOnInit() {
    this.route
      .params
      .subscribe(params => {
        if (this.contextStateService.filteredProjectList.length > 0) {
          this.currentPosition = this.contextStateService.filteredProjectList.map(function (e) {
            return e.id
          }).indexOf(Number(this.route.snapshot.params['id']));
        }
        const id: number = params['id'];
        this.projectService.getDetailOfProject(id).subscribe(res => {
          this.projectSrc.emit(res);
          this.projectName = res.name.toString();
          this.description = res.description.toString();
          this.tableDataSource.data = res.properties;

          this.projectService.getCompatibleVersions(id).subscribe(res => {
            this.versions = res;
          });
          this.projectService.getRelatedFiles(id).subscribe(res => {
            this.files = res;
          });
          this.projectService.getResourceDocuments(id).subscribe(res => {
            this.resources = res;
          });
        });
      });
  }

  loadFile(file) {
    this.projectService.getFileById(file.id).subscribe(res => {
      FileSaver.saveAs(res, file.fileName);
    }, err => {
      // this._service.error('Téléchargement fichier associé', 'Erreur lors du téléchargement du fichier associé', this.notifParams.notif);
    });
  }

  loadResource(resource) {
    this.projectService.getResourceDocumentById(resource.id).subscribe(res => {
      FileSaver.saveAs(res, resource.fileName);
    }, err => {
      // this._service.error('Téléchargement fichier associé', 'Erreur lors du téléchargement du fichier associé', this.notifParams.notif);
    });
  }

  back() {
    this.router.navigate(["/pages/projet/listProjets"]);
  }

  onNodeSelect(node: ProjectTreeNode) {
    this.activeNode= node;
    if (node.payload?.description) {
      this.description = node.payload.description.toString();
    } else {
      this.description = node.name + ' (pas de description)';
    }
  }

  routeTo(index) {
    this.router.navigate(["/pages/projet/projetDetail/" + this.contextStateService.filteredProjectList[index].id]);
  }
}
