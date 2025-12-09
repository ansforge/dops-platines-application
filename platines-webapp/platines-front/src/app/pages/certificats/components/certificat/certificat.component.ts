///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {Component, ElementRef, Input, OnInit, ViewChild} from '@angular/core';
import {ProjectLibraryService} from '../../../../services/projectlibrary.service';
import {DateConverterService} from '../../../../services/dateconverter.service';
import {ActivatedRoute, Router} from '@angular/router';
import {TestCertificate} from '../../../entity/testcertificate';
import {NotifParams} from '../../../entity/notifparams';
import {ModalPasswordComponent} from '../../../ui/components/modalpassword/modalpassword.component';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {MODAL_CONSTANT} from "../../../entity/constant";
import { CURRENT_USER_KEY } from '../../../../services/authentication.service';

@Component({
  selector: 'certificat',
  templateUrl: './certificat.html',
  styleUrls: ['./certificat.scss'],
})
export class CertificatComponent implements OnInit {
  @Input() multiple: boolean = false;
  fileNameSnapshot: string;
  @ViewChild("certifForm")
  certifForm;
  notifParams = new NotifParams();
  user = JSON.parse(localStorage.getItem(CURRENT_USER_KEY));//TODO review this to use the Profile object from AuthenticationService
  details: boolean = false;
  visible: string = 'true';
  validityDate: string;
  certificateStates = [{
    label: "VALID",
    value: "valide"
  }, {
    label: "OBSOLETE",
    value: "expiré"
  }, {
    label: "REVOCATED",
    value: "révoqué"
  }, {
    label: "INVALID",
    value: "invalide"
  }]
  certificate: TestCertificate = new TestCertificate();
  password: string;
  filename: String;
  isNew = true;
  id = null;
  userStock = JSON.parse(localStorage.getItem(CURRENT_USER_KEY));
  @ViewChild('fileInput')
  private inputFile: ElementRef;

  constructor(private projectLibraryService: ProjectLibraryService, private route: ActivatedRoute, private router: Router, private dateConverter: DateConverterService, private modalService: NgbModal) {

  }

  ngOnInit() {
    this.route
      .queryParams
      .subscribe(params => {
        if (params['id'] !== undefined) {
          this.isNew = false;
          this.details = true;
          this.id = params['id'];
          this.projectLibraryService.getTestCertificateById(params['id'])
            .subscribe(res => {
              this.filename = res.fileName;
              this.fileNameSnapshot = res.fileName;
              this.validityDate = this.dateConverter.convertdate(new Date(res.validityDate));
              this.certificate.validityDate = res.validityDate;
              this.certificate.description = res.description;
              this.certificate.state = res.state;
              // pour permettre de cocher automatiquement la case dans l'IHM
              if (res.downloadable) {
                this.certificate.downloadable = 'true';
              } else {
                this.certificate.downloadable = 'false';
              }
              this.certificate.fileName = res.fileName;
              this.details = true;
            }, err => {
            });
        } else {
          this.details = false;
          this.isNew = true;
        }
      });
  }

  selectFile() {
    const inputEl: HTMLInputElement = this.inputFile.nativeElement;
    const fileCount: number = inputEl.files.length;

    if (fileCount > 0) { // a file was selected
      this.certificate.file = inputEl.files.item(0);
      this.certificate.fileName = inputEl.files.item(0).name;
      this.analyze();
    } else {
      this.details = false;
      this.filename = ``;
    }

  }

  analyze() {
    const activeModal = this.modalService.open(ModalPasswordComponent, {size: 'lg'});
    activeModal.componentInstance.modalHeader = MODAL_CONSTANT.MODAL_HEADER_PASSWORD;
    activeModal.componentInstance.modalContent = MODAL_CONSTANT.MODAL_CONTENT_PASSWORD;
    activeModal.result.then((res) => {
      if (res.password !== undefined) {
        this.password = res.password;
        const inputEl: HTMLInputElement = this.inputFile.nativeElement;
        const fileCount: number = inputEl.files.length;
        const formData = new FormData();
        formData.append('file', this.certificate.file);
        formData.append('password', this.password);
        this.filename = this.certificate.fileName;
        this.projectLibraryService.analyzeCertificate(formData)
          .subscribe(res => {
            this.certificate.validityDate = res;
            this.validityDate = this.dateConverter.convertdate(new Date(res));
            this.details = true;
          }, err => {
            this.analyze();
          });
      }
    });
  }

  saveCertificate() {
    const request: TestCertificate = new TestCertificate();
    const formData = new FormData();
    if (!this.certifForm.invalid) {
      request.description = this.certificate.description;
      request.downloadable = this.certificate.downloadable;
      request.state = this.certificate.state;
      request.validityDate = new Date(this.certificate.validityDate).getTime();
      if (this.id !== null) {
        request.id = this.id;
      }
      request.fileName = this.certificate.fileName;
      formData.append('request', JSON.stringify(request));
      if (this.certificate.file !== undefined) {
        formData.append('file', this.certificate.file);
        formData.append('password', this.password);
      }
      if (this.id !== null) {
        this.projectLibraryService.edit(formData)
          .subscribe(res => {
            this.back();
          }, err => {
          });
      } else {
        this.projectLibraryService.createTestCertificate(formData)
          .subscribe(res => {
            this.back();
          }, err => {
          });
      }
    }
  }

  back() {
    this.router.navigate(['/pages/certificats/listCertificats']);
  }
}
