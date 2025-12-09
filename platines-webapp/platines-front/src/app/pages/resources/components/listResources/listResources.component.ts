///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {ApplicationRef, Component, EventEmitter, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {ResourcesService} from '../../../../services/resources.service';
import {ProfileService} from '../../../../services/profile.service'
import {NotifParams} from '../../../entity/notifparams';
import {ProjectLibraryService} from '../../../../services/projectlibrary.service';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {FileSystemDirectoryEntry, FileSystemFileEntry} from 'ngx-file-drop';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Resource} from '../../../entity/resource';
import {ConfirmModalComponent} from '../../../ui/components/confirmModal/confirmModal.component';
import {v4 as uuid} from 'uuid';
import {FilterListService} from '../../../../services/filterlist.service';
import {Theme} from '../../../entity/theme';
import {System} from '../../../entity/system';
import {Version} from '../../../entity/version';
import {MatTableDataSource} from "@angular/material/table";
import {SelectionModel} from "@angular/cdk/collections";
import {lastValueFrom, throwError} from "rxjs";
import {MatTreeNestedDataSource} from "@angular/material/tree";
import {NestedTreeControl} from "@angular/cdk/tree";
import {PlatineModalService} from "../../../ui/components/modal/platine-modal.service";
import {ProjectListTreeNode} from "../../../projet/component/listProjets/project-list-tree/project-list-tree.model";
import {TranslateService} from "@ngx-translate/core";
import { CURRENT_USER_KEY } from '../../../../services/authentication.service';

export interface ResourceFile extends File {
  resourceType: string;
  uuid: string;
}

export class XSystem extends System {
  versions: Array<Version>;
}

export class XTheme extends Theme {
  systems: Array<XSystem>;
}

@Component({
  selector: 'listResources',
  templateUrl: './listResources.html',
  styleUrls: ['./listResources.scss'],
})

export class ListResourcesComponent implements OnInit {
  roles = ['DOCUMENT', 'RESOURCE'];
  filesTableDataSource: MatTableDataSource<any> = new MatTableDataSource();
  filesTableSelection: SelectionModel<any> = new SelectionModel<any>(true, []);
  autoColumns = ['fileName', 'resourceType'];
  allColumns = [...this.autoColumns, 'checkbox'];
  notifParams: NotifParams = new NotifParams();
  userStock = JSON.parse(localStorage.getItem(CURRENT_USER_KEY));//TODO review this to use the Profile object from AuthenticationService
  root;
  nodes = [];
  description: string = "Veuillez sélectionner un élément dans l'arborescence";
  associationId;
  files: any[] = [];
  headers: HttpHeaders;
  // configObject: GtConfig<any>;
  resourcesList: Array<Resource> = [];
  touchedResources: Array<Resource> = [];
  filesToUpload: ResourceFile[] = [];
  filesToDelete: Array<Resource> = [];
  families: Array<XTheme> = [];
  block = true;
  touched = false;
  errorFiles = [];
  scope = '';
  protected activeNode: any;
  resourceSrc: EventEmitter<any> = new EventEmitter<any>();

  constructor(private appRef: ApplicationRef,
              private http: HttpClient,
              private filterListService: FilterListService,
              private projectService: ProjectLibraryService,
              private resourceService: ResourcesService,
              private router: Router,
              private modalService: NgbModal,
              private platinesModalService: PlatineModalService,
              private route: ActivatedRoute,
              private profileService: ProfileService,
              private translateService: TranslateService) {
  }

  ngOnInit() {
    this.projectService.getFamiliesAsTree().subscribe(res =>{
      this.resourceSrc.emit(res);
    })
  }

  getAncestors(node) {
    if(node.parent){
      return [...this.getAncestors(node.parent), node];
    }else{
      return [node];
    }
  }

  setScope(node:ProjectListTreeNode) {
    let ancestors = this.getAncestors(node);
    let scope = '';
    ancestors.forEach(ancestor => {
      scope += ancestor.name + ' > ';
    });
    this.scope = scope.substring(0, scope.length - 3);
  }

  onEvent($event) {
    let event = $event.node;
    this.activeNode = event;
    this.setScope(event);
    this.description = event.payload.description;
    this.associationId = event.payload.id;
    this.files = [];
    this.touched = true;
    this.resourceService.fetchResourcesByAssociation(this.associationId).subscribe(data => {
      this.filesTableDataSource.data = data;
    });
  }

  dropped(event: any) {
    this.files = event;
    for (const droppedFile of event) {

      // Is it a file?
      if (droppedFile.fileEntry.isFile) {
        //Check that a file of the same name is not present in the filesToUpload or files table of the current node
        let fileExists = false;
        this.filesToUpload.forEach(file => {
          if (file.name === droppedFile.fileEntry.name) {
            fileExists = true;
          }
        });
        this.filesTableDataSource.data.forEach(file => {
          if (file.fileName === droppedFile.fileEntry.name) {
            fileExists = true;
          }
        });
        if (fileExists) {
          this.platinesModalService.openInfoModal(
            {
              modalHeader: 'Nom du fichier dupliqué',
              modalContent: droppedFile.fileEntry.name + ' existe déjà dans ce scope.'
            }
          );
          continue;
        }
        const fileEntry = droppedFile.fileEntry as FileSystemFileEntry;
        fileEntry.file((file: ResourceFile) => {
          file.uuid = uuid();
          file.resourceType = "";
          this.updateTable(file);
          this.filesToUpload.push(file);
        });
      } else {
        // It was a directory (empty directories are added, otherwise only files)
        const fileEntry = droppedFile.fileEntry as FileSystemDirectoryEntry;
        console.log(droppedFile.relativePath, fileEntry);
      }
    }
  }

  updateTable(file: ResourceFile) {
    const resource = new Resource();
    resource.uuid = file.uuid
    resource.fileName = file.name;
    resource.fileType = "";
    this.filesTableDataSource.data.push(resource);
    this.filesTableDataSource.data = this.filesTableDataSource.data.slice();
    this.appRef.tick();
  }

  reset() {
    this.filesTableSelection.clear();
    this.touchedResources = [];
    this.filesToDelete = [];
    this.filesToUpload = [];
  }

  delete() {
    const listToDelete: Resource[] = this.filterListService.filterListForDelete(this.filesTableSelection.selected, this.filesTableDataSource.data);
    this.filesToDelete = this.filesToDelete.concat(listToDelete);
    const shadowResources = this.filesToDelete;
    //Remove files that are both in the filesToUpload list and in the filesToDelete list from both of them and also from the filesTableDataSource
    this.filesToDelete = this.filesToDelete.filter(fileToDelete => !this.filesToUpload.map(f => f.uuid).includes(fileToDelete.uuid));
    this.filesToUpload.forEach(fileToUpload => {
      if (shadowResources.map(f => f.uuid).includes(fileToUpload.uuid)) {
        this.filesToUpload = this.filesToUpload.filter(file => file.uuid != fileToUpload.uuid);
        this.filesTableDataSource.data = this.filesTableDataSource.data.filter(resource => resource.uuid != fileToUpload.uuid);
      }
    });
  }

  validate() {

    this.filesToUpload.forEach(file => {
      this.filesTableDataSource.data.filter(element => {
        if (element.uuid == file.uuid) {
          file.resourceType = element.resourceType;
          element.fileType = element.resourceType;
        }
      });
    });
    this.block = this.filesToUpload.filter(file => file.resourceType === undefined || file.resourceType === null || file.resourceType === "").length > 0;
    if ((this.filesToDelete.length || this.touchedResources.length || this.filesToUpload.length) && !this.block) {
      const activeModal = this.modalService.open(ConfirmModalComponent, {size: 'lg'});
      activeModal.componentInstance.modalHeader = 'Validation des modifications';
      activeModal.componentInstance.modalContent = 'Souhaitez-vous valider les modifications effectuées ?';
      activeModal.result.then(res => {
        if (res === 'confirm') {
          let promises: any[] = [];
          this.touchedResources.forEach(resource => {
            if (!this.filesToUpload.map(f => f.uuid).includes(resource.uuid)) {
              promises.push(lastValueFrom(this.resourceService.updateResource(resource)))
            }
          });
          this.filesToDelete.forEach(fileToDelete => {
            this.filesToUpload = this.filesToUpload.filter(element => element.uuid != fileToDelete.uuid);
            promises.push(this.resourceService.deleteResource(fileToDelete).subscribe(
              () => {
                this.filesTableDataSource.data = this.filesTableDataSource.data.filter(element => element.id != fileToDelete.id);
              }, error => {
                throwError(() => new Error(error))
              }
            ))
          });
          this.filesToUpload.forEach(fileToUpload => {
            const formData = new FormData();
            formData.append('resourceFile', fileToUpload);
            formData.append('associationId', this.associationId);
            formData.append('resourceType', fileToUpload.resourceType);
            promises.push(this.resourceService.addResource(formData).subscribe(
                res => {
                  this.filesTableDataSource.data.filter(element => {
                    if (element.uuid == fileToUpload.uuid) {
                      element.id = res;
                    }
                  });
                }, error => {
                  this.platinesModalService.openInfoModal(
                    {
                      modalHeader: 'Nom du fichier dupliqué',
                      modalContent: fileToUpload.name + ' existe déjà dans ce scope.'
                    });
                  this.filesTableDataSource.data = this.filesTableDataSource.data.filter(element => element.uuid != fileToUpload.uuid);
                }
              )
            );
          });
          Promise.allSettled(promises).then((values) => {
            console.log("All promises settled, values:", values);
            this.reset();
          });
        }
      })
    }
  }

  async saveResource(formData) {
    return await this.resourceService.addResource(formData).toPromise();
  }

  setAll(element) {
    if (this.isAllSelected() || this.filesTableSelection.hasValue()) {
      this.filesTableSelection.clear();
      element.target.checked = false;
    } else {
      this.filesTableDataSource.data.forEach(row => this.filesTableSelection.select(row));
    }
  }

  setRow(element) {
    this.filesTableSelection.toggle(element);
  }

  isAllSelected() {
    if (this.filesTableDataSource.data.length == 0) return false;
    const numSelected = this.filesTableSelection.selected.length;
    const numRows = this.filesTableDataSource.data.length;
    return numSelected === numRows;
  }

  markAsTouched(element) {
    this.touchedResources.push(element);
  }

  getAvailableRoles(element) {
    let roles: Map<string, string> = new Map<string, string>();

    switch (element?.fileName?.split('.').pop()) {
      case "doc":
      case "docx":
      case "pdf":
      case "txt":
        roles.set("RESOURCE", "Ressource");
        roles.set("DOCUMENT", "Documentation");
        break;
      case "zip":
        roles.set("BUNDLE_JAR", "Bundle Jar");
        roles.set("NOMENCLATURE", "Nomenclature");
        break;
      case "jar":
        roles.set("PLUGIN", "Plugin");
        roles.set("SINGLE_JAR", "Single Jar");
        break;
      default:
        roles.set("RESOURCE", "Ressource");
        break;
    }

    return roles;
  }
}


