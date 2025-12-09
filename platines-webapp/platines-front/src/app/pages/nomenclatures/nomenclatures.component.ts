///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {Router} from '@angular/router';
import {NomenclatureService} from '../../services/nomenclature.service';
import {ProfileService} from '../../services/profile.service'
import {NotifParams} from '../entity/notifparams';
import {Nomenclature} from '../entity/nomenclature';

@Component({
  selector: 'nomenclatures',
  templateUrl: './nomenclatures.html',
  styleUrls: ['./nomenclatures.scss'],
})
export class NomenclaturesComponent implements OnInit {
  @ViewChild('fileInput')
  private inputFile: ElementRef;
  @ViewChild('nomenclatureForm')
  nomenclatureForm;
  notifParams = new NotifParams();
  nomenclature = new Nomenclature();
  date: string = null;

  constructor(/*private http: Http,*/ private router: Router,
    /*private _service: NotificationsService,*/ private nomenclatureService: NomenclatureService, private profileService: ProfileService) {


  }

  ngOnInit() {
    this.nomenclatureService.getNomenclatureLast().subscribe(res => {
      let date = new Date(res);
      let dateUTC = Date.UTC(date.getFullYear(), date.getMonth(), date.getDate(), date.getHours(), date.getMinutes(), date.getSeconds());
      this.date = new Date(dateUTC).toLocaleString();
    }, err => {
      this.date = null;
    });
  }

  createNomenclature() {
    const formData = new FormData();
    formData.append('zipFile', this.nomenclature.file );
    formData.append('fileName', this.nomenclature.fileName);

    this.nomenclatureService.createNomenclature(formData)
      .subscribe(res => {
        //this._service.success(SUCCESS_CONSTANT.SUCCESS_TITLE_NOMENCLATURE, SUCCESS_CONSTANT.SUCCESS_CREATE_NOMENCLATURE, this.notifParams.notif);
        this.nomenclatureService.getNomenclatureLast().subscribe(res => {
          let date = new Date(res);
          let dateUTC = Date.UTC(date.getFullYear(), date.getMonth(), date.getDate(), date.getHours(), date.getMinutes(), date.getSeconds());
          this.date = new Date(dateUTC).toLocaleString();
        });
    }, err => {
      //this._service.error(ERROR_CONSTANT.ERROR_MESSAGE_TITLE, ERROR_CONSTANT.ERROR_MESSAGE_CREATE_NOMENCLATURE, this.notifParams.notif);
    });
  }

  selectFile() {
    const inputEl: HTMLInputElement = this.inputFile.nativeElement;
    const fileCount: number = inputEl.files.length;
    if (fileCount > 0) { // a file was selected
      for (let i = 0; i < fileCount; i++) {
        this.nomenclature.fileName = inputEl.files.item(i).name;
        this.nomenclature.file = inputEl.files.item(i);
      }
    }
  }

}

