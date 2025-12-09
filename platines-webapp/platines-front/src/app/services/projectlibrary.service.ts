///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {Injectable} from "@angular/core";
import {HttpClient, HttpErrorResponse, HttpHeaders} from "@angular/common/http";
import {TestCertificate} from '../pages/entity/testcertificate';
import {Theme} from '../pages/entity/theme';
import {ProjectList} from '../pages/entity/projectlist';
import {environment} from '../../environments/environment';
import {forkJoin, Observable, catchError, of, merge} from 'rxjs';
import {System} from "../pages/entity/system";
import {Version} from "../pages/entity/version";
import {Project} from "../pages/entity/project";
import {ProfileService} from './profile.service';
import {Certificate} from "../pages/entity/certificate";
import * as FileSaver from "file-saver";
import {PlatineModalService} from "../pages/ui/components/modal/platine-modal.service";
import {FileService} from "./file.service";
import {ProjectDetails} from "../pages/entity/projectDetail";
import {FamiliesRoles} from "../pages/entity/familyRoleCoverage";
import {ProjectTreeNode} from "../pages/projet/component/projetDetail/project-tree/project-tree.model";
import {TranslateService} from "@ngx-translate/core";

@Injectable()
export class ProjectLibraryService {

  headers: HttpHeaders;
  
  constructor(
    private http: HttpClient,
    private modalService: PlatineModalService,
    private fileService: FileService,
    private translateService: TranslateService
  ) {
  }

  createHeadersMultipart() {
    this.headers = new HttpHeaders();
    this.headers = this.headers.append('enctype', 'multipart/form-data;charset=UTF-8');
  }

  createHeadersDownload() {
    this.headers = new HttpHeaders();
    this.headers.append('Content-Type', 'Application/octet-stream');
  }

  /*
   * TODO : have a look at the relevance of putting this here...
   * certificates are not projects, aven though they are used by projects
   */
  getTestCertificates(): Observable<TestCertificate[]> {
    return this.http.get<TestCertificate[]>(`${environment.API_ENDPOINT_SECURE}testCertificate/getAll`).pipe();
  }

  deleteTestCertificate(id: Number) {
    return this.http.delete(`${environment.API_ENDPOINT_SECURE}testCertificate/delete/${id}`);
  }

  createTestCertificate(certificateCreate: FormData) {
    this.createHeadersMultipart();
    return this.http.post(`${environment.API_ENDPOINT_SECURE}testCertificate/create`, certificateCreate, {headers: this.headers});
  }

  analyzeCertificate(certificateAnalyze: FormData): Observable<any> {
    this.createHeadersMultipart();
    return this.http.post(`${environment.API_ENDPOINT_SECURE}testCertificate/analyze`, certificateAnalyze, {headers: this.headers});
  }

  getTestCertificateById(id: Number): Observable<TestCertificate> {
    return this.http.get<TestCertificate>(`${environment.API_ENDPOINT_SECURE}testCertificate/get/${id}`);
  }

  edit(certificateEdit: FormData) {
    this.createHeadersMultipart();
    return this.http.post(`${environment.API_ENDPOINT_SECURE}testCertificate/edit`, certificateEdit, {headers: this.headers});
  }

  getFamiliesAsTree(): Observable<Theme[]> {
    return this.http.get<Theme[]>(`${environment.API_ENDPOINT_SECURE}family/getAllTree`).pipe();
  }

  getFamilies(): Observable<Theme[]> {
    return this.http.get<Theme[]>(`${environment.API_ENDPOINT_SECURE}family/getAll`).pipe(
      catchError(
        (err: any, caught: Observable<Theme[]>) => {
          this.modalService.openErrorModal({
            modalHeader: 'Erreur serveur',
            err: err
          });
          return of([]);
        }
      )
    );
  }

  getFamiliesRoleCoverage(): Observable<FamiliesRoles[]> {
    return this.http.get<FamiliesRoles[]>(`${environment.API_ENDPOINT_SECURE}family/role-coverage/getAll`).pipe(
      catchError(
        (err: any, caught: Observable<FamiliesRoles[]>) => {
          this.modalService.openErrorModal({
            modalHeader: 'Erreur serveur',
            err: err
          });
          return of([]);
        }
      )
    );
  }

  deleteFamily(id: Number) {
    return this.http.delete(`${environment.API_ENDPOINT_SECURE}family/delete/${id}`)
  }

  getSystemByFamilyId(id: Number): Observable<System[]> {
    return this.http.get<System[]>(`${environment.API_ENDPOINT_SECURE}system/getService/${id}`);
  }

  getSystemByFamilyIdWithRole(id: Number, role: string): Observable<System[]> {
    return this.http.get<System[]>(`${environment.API_ENDPOINT_SECURE}system/getService/${id}?coveredRole=${role}`)
      .pipe(
        catchError(
          (err: any, caught: Observable<System[]>) => {
            this.modalService.openErrorModal({
              modalHeader: 'Erreur serveur',
              err: err
            });
            return of([]);
          }
        )
      );
  }

  getFamilyById(id: Number): Observable<Theme> {
    return this.http.get<Theme>(`${environment.API_ENDPOINT_SECURE}family/get/${id}`);
  }

  createFamily(familyDto: Theme) {
    return this.http.post(`${environment.API_ENDPOINT_SECURE}family/create`, familyDto);
  }

  updateFamily(familyDto: Theme) {
    return this.http.post(`${environment.API_ENDPOINT_SECURE}family/update`, familyDto);
  }

  getSystems(): Observable<System[]> {
    return this.http.get<System[]>(`${environment.API_ENDPOINT_SECURE}system/getAll`).pipe();
  }

  getSystemById(id: Number): Observable<any> {
    return this.http.get<System>(`${environment.API_ENDPOINT_SECURE}system/get/${id}`).pipe();

  }

  createSystem(systemDto: System) {
    return this.http.post(`${environment.API_ENDPOINT_SECURE}system/create`, systemDto);
  }

  updateSystem(systemDto: System) {
    return this.http.post(`${environment.API_ENDPOINT_SECURE}system/update`, systemDto);
  }

  getAllVersions(): Observable<Version[]> {
    return this.http.get<Version[]>(`${environment.API_ENDPOINT_SECURE}version/getAll`).pipe();
  }

  getVersionsByFamilyId(id: Number): Observable<Version[]> {
    return this.http.get<Version[]>(`${environment.API_ENDPOINT_SECURE}version/getVersions/${id}`);
  }

  getAllProjects(): Observable<ProjectList[]> {
    return this.http.get<ProjectList[]>(`${environment.API_ENDPOINT_SECURE}project/getAll`)
  }

  analyzeProject(form: FormData): Observable<Project> {
    this.createHeadersMultipart();
    return this.http.post<Project>(`${environment.API_ENDPOINT_SECURE}project/analyze`, form, {headers: this.headers});
  }

  addProject(formData: FormData): Observable<any> {
    this.createHeadersMultipart();
    return this.http.post(`${environment.API_ENDPOINT_SECURE}project/add`, formData, {headers: this.headers});
  }

  editProject(formData: FormData): Observable<any> {
    this.createHeadersMultipart();
    return this.http.post(`${environment.API_ENDPOINT_SECURE}project/update`, formData, {headers: this.headers});
  }

  deleteProject(idProject: Number): Observable<any> {
    return this.http.delete(`${environment.API_ENDPOINT_SECURE}project/delete/${idProject}`);
  }

  getProjectById(idProject: Number): Observable<Project> {
    return this.http.get<Project>(`${environment.API_ENDPOINT_SECURE}project/get/${idProject}`)
  }

  downloadProject(idProject: Number): void {
    this.http.get<{ file: string, fileName: string }>(`${environment.API_ENDPOINT_SECURE}project/download/${idProject}`).subscribe({
        next: res => {
          const fileName = res.fileName;
          const blob = this.fileService.b64toBlob(res.file, 'application/octet-stream');
          FileSaver.saveAs(blob, fileName);
        },
        error: (err: HttpErrorResponse) => {
          this.modalService.openErrorModalWithCustomMessage({
            modalHeader: this.translateService.instant("pages.projects.errors.project_download_title"),
            modalContent: this.translateService.instant("pages.projects.errors.project_download_msg_"+err.status)
          })
        }
      }
    );
  }

  downloadTestCertificate(idTestCertificate: Number): void {
    this.http.get<Certificate>(`${environment.API_ENDPOINT_SECURE}project/certificate/download/${idTestCertificate}`).subscribe({
        next: (res: Certificate) => {
          const cert = res;
          const fileExt = cert.fileName.split('.').pop();
          cert.fileName = cert.fileName.replace(fileExt, 'crt');
          const publickey = [];
          publickey.push(cert.pem);
          const blob = new Blob(publickey, {type: 'application/x-x509-ca-cert'});
          FileSaver.saveAs(blob, cert.fileName.toString());
        },
        error: (err: HttpErrorResponse) => {
          this.modalService.openErrorModalWithCustomMessage({
            modalHeader: this.translateService.instant("pages.projects.errors.certificate_download_title"),
            modalContent: this.translateService.instant("pages.projects.errors.certificate_download_msg_"+err.status)
          })
        }
      }
    );
  }

  getDetailOfProject(idProject: Number): Observable<ProjectDetails> {
    return this.http.get<ProjectDetails>(`${environment.API_ENDPOINT_SECURE}project/detail/${idProject}`);
  }

  getCompatibleVersions(idProject: Number): Observable<any> {
    return this.http.get(`${environment.API_ENDPOINT_SECURE}project/versions/${idProject}`)
  }

  sortVersion(version: Version[]): Version[] {
    version.forEach(element => {
      const ver = `${element.service.theme.name} > ${element.service.name} > ${element.label}`;
      element.label = ver;

    });
    version.sort((a: any, b: any) => {
      if (a.name < b.name) {
        return -1;
      } else if (a.libelle > b.libelle) {
        return 1;
      } else {
        return 0;
      }
    });
    return version;
  }

  getProjectByVersion(idVersion: Number): Observable<ProjectList[]> {
    return this.http.get<ProjectList[]>(`${environment.API_ENDPOINT_SECURE}project/getByVersion/${idVersion}`)
  }

  getVersion(id: Number): Observable<Version> {
    return this.http.get<Version>(`${environment.API_ENDPOINT_SECURE}version/get/${id}`);
  }

  createVersion(version: Version) {
    return this.http.post(`${environment.API_ENDPOINT_SECURE}version/create`, version);
  }

  updateVersion(version: Version) {
    return this.http.post(`${environment.API_ENDPOINT_SECURE}version/update`, version);
  }

  mapping(ids: FormData): Observable<any> {
    this.createHeadersMultipart();
    return this.http.post(`${environment.API_ENDPOINT_SECURE}compatibility/mapping`, ids, {headers: this.headers});
  }

  getVersionBySystem(idSystem: Number): Observable<Version[]> {
    return this.http.get<Version[]>(`${environment.API_ENDPOINT_SECURE}compatibility/versions/${idSystem}`);
  }

  getVersionBySystemWithRole(idSystem: Number, coveredRole: string) {
    return this.http.get<Version[]>(`${environment.API_ENDPOINT_SECURE}compatibility/versions/${idSystem}?coveredRole=${coveredRole}`)
      .pipe(
        catchError(
          (err: any, caught: Observable<Version[]>) => {
            this.modalService.openErrorModal(
              {
                err: err,
                modalHeader: 'Erreur serveur'
              }
            );
            return of([]);
          }
        )
      );
  }

  getRelatedFiles(idProject: Number): Observable<File[]> {
    return this.http.get<any[]>(`${environment.API_ENDPOINT_SECURE}project/files/${idProject}`);
  }

  getFileById(idFile: Number): Observable<any> {
    this.createHeadersDownload();
    return this.http.get(`${environment.API_ENDPOINT_SECURE}project/file/load/${idFile}`, {
      headers: this.headers,
      responseType: 'blob'
    });
  }

  getResourceDocuments(idProject: Number): Observable<any[]> {
    return this.http.get<any[]>(`${environment.API_ENDPOINT_SECURE}project/resourceDocuments/${idProject}`);
  }

  getResourceDocumentById(idResource: Number): Observable<any> {
    this.createHeadersDownload();
    return this.http.get(`${environment.API_ENDPOINT_SECURE}project/resourceDocument/load/${idResource}`, {
      headers: this.headers,
      responseType: 'blob'
    });
  }

  /**
   * Returns an array of observables containing all the projects of the child
   * versions of the given node
   * @param node
   */
  getProjectsByNode(node: ProjectTreeNode) {
    let versions: Version[] = [];
    this.getVersionsFromNode(node, versions);
    let observables: Observable<any>[] = versions.map(version =>
      this.getProjectByVersion(version.id));
    if (observables.length > 0) {
      return forkJoin(observables);
    } else {
      return of([]);
    }
  }

  /**
   * Returns an array of observables containing all the projects of the child
   * versions of every node in the given array, removing duplicates
   * @param nodeArray
   */
  getProjectsByNodeArray(nodeArray: ProjectTreeNode[]): Observable<any[]> {
    let observables: Observable<any>[] = [];
    nodeArray.forEach(node => {
      let result = this.getProjectsByNode(node);
      observables.push(result);
    });
    if (observables.length > 0) {
      return merge(...observables)
    }else{
      return of([]);
    }
  }

  getVersionsFromNode(node: ProjectTreeNode, versions: Version[]): void {
    if (node.hasChildren()) {
      node.children.forEach(child => {
        this.getVersionsFromNode(child, versions);
      });
    } else {
      if (node.parent?.parent) {
        versions.push(node.payload as unknown as Version);
      } else {
        return null;
      }
    }
  }
}
