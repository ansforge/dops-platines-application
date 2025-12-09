///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {Component, OnInit} from '@angular/core';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'projects-modal',
  styleUrls: ['./projectsModal.scss'],
  templateUrl: './projectsModal.html',
})
export class ProjectsModalComponent implements OnInit {
  modalHeader: string;
  modalContent: string;
  url: string;
  projectsList;
  properties = [];
  projectM;
  isSelected;
  ngOnInit() {
    if(this.projectM !== undefined) {
      this.properties = this.projectM.projectProperties;
      if (this.properties.length > 0) {
        this.isSelected = true;
      }
    } else {
      this.projectM = {};
    }
  }

  constructor(public activeModal: NgbActiveModal) {
  }


  closeModal() {
    this.activeModal.close();
  }

  validate() {
    this.activeModal.close({project: this.projectM, properties: this.properties});
  }
  getProjectProperties(projectS) {
    let idProject = Number.parseInt(projectS);
    this.projectM = this.projectsList.find(p => p.id === idProject)
    this.properties = this.projectM.properties;
    if (this.properties.length > 0) {
      this.isSelected = true;
      this.properties.forEach(prop => {
        if (prop.propertyType === 'ENDPOINT') {
          prop.value = this.url;
        }
      });
    }
  }

}
