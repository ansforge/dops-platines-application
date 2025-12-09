///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {Component, ElementRef, Input, OnInit, QueryList, ViewChild, ViewChildren} from '@angular/core';
import {UntypedFormBuilder, UntypedFormGroup} from '@angular/forms';
import {ActivatedRoute, Router} from '@angular/router';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {ConfirmModalComponent} from '../../../ui/components/confirmModal/confirmModal.component';
import {ProjectLibraryService} from '../../../../services/projectlibrary.service';

import {Project} from '../../../entity/project';
import {Version} from '../../../entity/version';
import {Property} from '../../../entity/property';
import {TestCertificate} from '../../../entity/testcertificate';
import {NotifParams} from '../../../entity/notifparams';
import {FilterListService} from '../../../../services/filterlist.service';
import {ResourcesService} from '../../../../services/resources.service';
import {ProfileService} from '../../../../services/profile.service';
import {MatTableDataSource} from '@angular/material/table';
import {SelectionModel} from '@angular/cdk/collections';
import {PlatineModalService} from "../../../ui/components/modal/platine-modal.service";
import {TranslateService} from "@ngx-translate/core";

@Component({
  selector: 'createProjet', templateUrl: './createProjet.html', styleUrls: ['./createProjet.scss'],
})
export class CreateProjetComponent implements OnInit {

  @Input() multiple: boolean = true;
  @ViewChild('fileInput') inputProjectFile: ElementRef;
  @ViewChildren('fileInputSch') inputFiles: QueryList<ElementRef>;

  @ViewChild('projectForm') projectForm;

  dataSourceProperties = new MatTableDataSource<any>;
  dataSourceFiles = new MatTableDataSource<any>;
  dataSourceResources = new MatTableDataSource<any>;
  autoColumnsProperties: string[] = [];
  allColumnsProperties: string[] = [...this.autoColumnsProperties, 'key', 'value', 'description', 'propertyType'];
  autoColumnsFiles: string[] = [];
  allColumnsFiles: string[] = [...this.autoColumnsFiles, 'fileName', 'dateUpload', 'fileType', 'checkbox'];
  autoColumnsResources: string[] = [];
  allColumnsResources: string[] = [...this.autoColumnsResources, 'fileName', 'dateUpload', 'fileType'];


  selectionProperties = new SelectionModel<any>(true, []);
  selectionFiles = new SelectionModel<any>(true, []);
  selectionResources = new SelectionModel<any>(true, []);
  notifParams: NotifParams = new NotifParams();
  project: Project = new Project();
  form: UntypedFormGroup;
  filename = `Choisissez votre fichier projet`;
  details: boolean = false;
  btnAnalyser: boolean = false;
  versionsSystem: Version[];
  selectedVersion: boolean;
  certificatTst: any[];
  listFileTransfert = [];
  id: number;
  isNew = false;
  versionLabel: string;
  isAdmin = false;
  fileTypes: Map<string, string> = new Map<string, string>([
    ['RESOURCE', 'Ressource'],
    ['DOCUMENT', 'Documentation'],
    ['SINGLE_JAR', 'Single jar'],
    ['BUNDLE_JAR', 'Bundle jar']
  ]);
  propertyTypes = [{
    key: 'NON_EDITABLE_VISIBLE',
    value: 'Non éditable visible'

  }, {
    key: 'EDITABLE_VISIBLE',
    value: 'Editable visible'
  }, {
    key: 'NON_EDITABLE_INVISIBLE',
    value: 'Non éditable invisible'
  }, {
    key: 'ENDPOINT',
    value: 'Endpoint'
  }];
  dateFormat: string = 'dd/MM/yyyy HH:mm:ss';
  labelsPath: string = "commons.labels.file";

  constructor(
    private profileService: ProfileService,
    public fb: UntypedFormBuilder,
    private router: Router,
    private route: ActivatedRoute,
    private modalService: NgbModal,
    private projectService: ProjectLibraryService,
    private filterListService: FilterListService,
    private resourceService: ResourcesService,
    private platinesModalService: PlatineModalService,
    private translateService: TranslateService) {
  }

  ngOnInit() {
    if (this.profileService.getProfileFromToken() === 'admin') {
      this.isAdmin = true;
    }

    this.route.queryParams.subscribe(params => {
      this.projectService.getTestCertificates().subscribe({
        next: res => this.certificatTst = res,
        error: () => this.platinesModalService.openErrorModalWithCustomMessage({
          modalHeader: this.translateService.instant("pages.projects.errors.loading_title"),
          modalContent: this.translateService.instant("pages.projects.errors.test_certificate_msg")
        })
      });

      if (params['id'] !== undefined) {
        this.details = true;
        this.btnAnalyser = false;
        this.id = params['id'];

        this.projectService.getProjectById(this.id).subscribe({
          next: res => {
            this.project = res;
            this.dataSourceFiles.data = this.project.relatedFiles.slice();
            const versionId = res.versions[res.versions.length - 1].id
            this.resourceService.fetchResourcesByVersion(versionId).subscribe({
              next: data => {
                this.dataSourceResources.data = data.filter(
                  element => ['DOCUMENT', 'RESOURCE', 'SINGLE_JAR', 'BUNDLE_JAR', 'NOMENCLATURE', 'PLUGIN'].includes(element.fileType)
                );
              },
              error: () => this.platinesModalService.openErrorModalWithCustomMessage({
                modalHeader: this.translateService.instant("pages.projects.errors.loading_title"),
                modalContent: this.translateService.instant("pages.projects.errors.resources_msg")
              })
            });
            this.projectService.getAllVersions().subscribe({
              next: res => {
                this.versionsSystem = this.projectService.sortVersion(res);
                this.versionLabel = this.versionsSystem.filter(vs => vs.id == this.project.versions[0].id)[0].label
              },
              error: () => this.platinesModalService.openErrorModalWithCustomMessage({
                modalHeader: this.translateService.instant("pages.projects.errors.loading_title"),
                modalContent: this.translateService.instant("pages.projects.errors.versions_msg")
              })
            });
            this.dataSourceProperties.data = this.project.properties;
            this.listFileTransfert = this.project.relatedFiles
          },
          error: () => void this.router.navigate(['/pages/projet/listProjets'])
        });

      } else {
        this.project = new Project();
        this.projectService.getAllVersions().subscribe({
          next: res => this.versionsSystem = this.projectService.sortVersion(res),
          error: () => this.platinesModalService.openErrorModalWithCustomMessage({
            modalHeader: this.translateService.instant("pages.projects.errors.loading_title"),
            modalContent: this.translateService.instant("pages.projects.errors.versions_msg")
          })
        });
        this.dataSourceFiles.data = this.project.relatedFiles.slice();
        this.dataSourceProperties.data = this.project.properties;
        this.listFileTransfert = this.project.relatedFiles
        this.details = false;
        this.btnAnalyser = false;
        this.isNew = true;
      }
    });
  }

  setRole(role) {
    this.project.role = role;
    this.setCertificate('null');
  }

  setCertificate(idCertificate) {
    this.project.testCertificate = new TestCertificate();
    this.project.testCertificate.id = idCertificate;
  }

  onChange(event: any) {

    const target: HTMLInputElement = event.currentTarget as HTMLInputElement;
    const files: FileList = target.files;

    for (let index = 0; index < files.length; index++) {
      const element = files[index];
      const date = new Date();
      const offset = date.getTimezoneOffset();
      const dateUTC = Date.UTC(
        date.getFullYear(), date.getMonth(), date.getDate(), date.getHours(),
        date.getMinutes() + offset, date.getSeconds(),
      );

      const fichierTransfert = {
        fileName: element.name, file: element, fileType: 'RESOURCE', dateUpload: dateUTC,
      };

      if (this.dataSourceResources.data.find(f => f.fileName === element.name)) {
        alert(element.name + ' existe déjà en tant que ressource')
      } else if (this.listFileTransfert.find(f => f.fileName === element.name)) {
        const activeModal = this.modalService.open(ConfirmModalComponent, {size: 'lg'});
        activeModal.componentInstance.modalHeader = 'Confirmation du remplacement';
        activeModal.componentInstance.modalContent = `Êtes-vous sûr de vouloir remplacer le fichier ci-dessous : ${element.name}`;
        activeModal.result.then((res) => {
          if (res === 'confirm') {
            const obj = this.listFileTransfert.find(f => f.fileName === element.name);
            const index4 = this.listFileTransfert.indexOf(obj);
            this.listFileTransfert[index4] = fichierTransfert;
            this.dataSourceFiles.data = this.dataSourceFiles.data.filter((u) => u.fileName !== fichierTransfert.fileName);
            this.dataSourceFiles.data.push(fichierTransfert);
            this.dataSourceFiles.data = this.dataSourceFiles.data.slice();
          }
        });
      } else {
        this.listFileTransfert.push(fichierTransfert);
        this.dataSourceFiles.data.push(fichierTransfert);
        this.dataSourceFiles.data = this.dataSourceFiles.data.slice();
      }
    }
  }

  deleteFiles() {
    const supressedFiles = this.filterListService.filterListForDelete(this.selectionFiles.selected, this.dataSourceFiles.data);
    for (const entry of supressedFiles) {
      this.dataSourceFiles.data = this.dataSourceFiles.data.filter((u) => u.fileName !== entry.fileName);
      const index = this.listFileTransfert.indexOf(entry, 0);
      if (index > -1) {
        this.listFileTransfert.splice(index, 1);
      }
    }
    this.selectionFiles.clear();
  }

  isVisible(value) {
    return eval(value);
  }

  setVersion(event) {
    this.project.versions = new Array<Version>();
    const version: Version = new Version();
    version.id = event;
    this.selectedVersion = (version.id >= 0);
    this.project.versions.push(version);
    this.project.fileName = undefined;
    this.details = false;
  }

  selectFile(event: any) {
    const target: HTMLInputElement = event.currentTarget as HTMLInputElement;
    const files: FileList = target.files;
    if (files.length > 0) {
      this.project.file = files.item(0);
      this.project.fileName = files.item(0).name;
      this.btnAnalyser = true;
      const formData2 = new FormData();
      formData2.append('file', this.project.file);
      this.projectService.analyzeProject(formData2).subscribe({
        next: res => {
          this.details = true;
          if (this.isNew) {
            this.setCertificate('null');
            this.project.visibility = !this.isAdmin;
          }
          this.project.description = res.description;
          this.project.name = res.name;
          this.project.role = res.role;
          const properties: Property[] = this.project.properties;
          this.project.properties = res.properties;
          this.project.properties.forEach(prop => {
            properties.forEach(prop2 => {
              if (prop.key == prop2.key) {
                prop.description = prop2.description;
                prop.id = prop2.id;
                prop.propertyType = prop2.propertyType;
                prop.value = prop2.value;
              }
            });
          })
          this.dataSourceProperties.data = this.project.properties;
          this.project.role = res.role;
          const versionId = this.project.versions[this.project.versions.length - 1].id
          this.resourceService.fetchResourcesByVersion(versionId).subscribe(data => {
            this.dataSourceResources.data = data.filter(
              element => ['DOCUMENT', 'RESOURCE', 'SINGLE_JAR', 'BUNDLE_JAR', 'NOMENCLATURE', 'PLUGIN'].includes(element.fileType)
            );
          });
        },
        error: () => {
          this.details = false;
          this.filename = ``;
          this.btnAnalyser = false;
          this.platinesModalService.openErrorModalWithCustomMessage({
            modalHeader: this.translateService.instant("pages.projects.errors.analysis_title"),
            modalContent: this.translateService.instant("pages.projects.errors.analysis_msg")
          })
        }
      })
    } else {
      this.details = false;
      this.filename = ``;
      this.btnAnalyser = false;
    }
  }

  sendTest() {
    this.projectForm.submitted = true;
    let propertySet: boolean = this.areAllPropertyTypesSet();
    if (propertySet) {
      const projectFile = this.project.file;
      const formData = new FormData();
      formData.append('projectFile', projectFile);
      this.listFileTransfert.forEach(element => {
        if (element.file !== undefined) {
          formData.append('relatedFiles', element.file);
        }
      });
      const map = [];
      this.listFileTransfert.forEach(element => {
        const fichierT = {
          fileName: element.fileName, fileType: element.fileType,
        }
        map.push(fichierT);
      })
      this.project.file = null;
      formData.append('mapFile', JSON.stringify(map));
      if (this.project.testCertificate !== null) {
        if (!this.isNew) {
          const idCertificate = this.project.testCertificate.id;
          this.project.testCertificate = new TestCertificate();
          this.project.testCertificate.id = idCertificate;
        }
        if (this.project.testCertificate.id.toString() === 'null') {
          this.project.testCertificate = null;
        }
      }
      this.project.relatedFiles = [];
      formData.append('project', JSON.stringify(this.project));
      if (this.isNew) {
        this.projectService.addProject(formData).subscribe(() => {
          void this.router.navigate(['/pages/projet/listProjets']);
        });
      } else {
        this.projectService.editProject(formData).subscribe(() => {
          void this.router.navigate(['/pages/projet/listProjets']);
        });
      }
    } else {
      this.platinesModalService.openErrorModalWithCustomMessage({
        modalHeader: this.translateService.instant("pages.projects.errors.invalid_form_title"),
        modalContent: this.translateService.instant("pages.projects.errors.invalid_form_msg")
      });
    }
  }

  back() {
    this.router.navigate(['/pages/projet/listProjets']);
  }

  setAllFiles(element) {
    if (this.isAllFilesSelected() || this.selectionFiles.hasValue()) {
      this.selectionFiles.clear();
      element.target.checked = false;
    } else {
      this.dataSourceFiles.data.forEach(row => this.selectionFiles.select(row));
    }
  }

  setRowFiles(element) {
    this.selectionFiles.toggle(element);
  }

  isAllFilesSelected() {
    const numSelected = this.selectionFiles.selected.length;
    const numRows = this.dataSourceFiles.data.length;
    if (numRows > 0) {
      return numSelected === numRows;
    } else {
      return false;
    }
  }

  isCertificateSetOrNotRequired() {
    return (!(this.project?.role === 'SERVER' && (this.project?.testCertificate === null || this.project?.testCertificate.id.toString() === 'null')));
  }

  areAllPropertyTypesSet() {
    for (let property of this.dataSourceProperties.data) {
      if (!property.propertyType) {
        return false;
      }
    }
    return true;
  }

  formInvalid() {
    return !this.areAllPropertyTypesSet() || !this.isCertificateSetOrNotRequired();
  }
}
