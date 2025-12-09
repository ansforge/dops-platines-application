///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

/**
 * (c) Copyright 1998-2023, ANS. All rights reserved.
 */
import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Theme } from "../../../../entity/theme";
import { ProjectLibraryService } from "../../../../../services/projectlibrary.service";
import { BulkUpdateService } from '../../bulkUpdateService.service';
import { PlatineModalService } from "../../../../ui/components/modal/platine-modal.service";

@Component({
  selector: 'app-bulk-update-choicebox',
  templateUrl: './bulk-update-choicebox.component.html',
  styleUrls: ['./bulk-update-choicebox.component.scss']
})
export class BulkUpdateChoiceboxComponent implements OnInit{
  archive:{
    name: string;
    data: Blob;
  };
  themes: Theme[]=[];
  themeId: number;

  constructor(
    private activeModal: NgbActiveModal,
    private modalService: PlatineModalService,
    private bulkUpdateService: BulkUpdateService,
    private projectService: ProjectLibraryService,
    private router: Router,
    private activeRoute: ActivatedRoute
  ){}

  ngOnInit(): void {
    this.projectService.getFamilies().subscribe(
      res => this.themes =  res
    );
  }

  onCancel(): void{
    this.activeModal.close();
  }

  onDryRun(): void {
    this.bulkUpdateService.bulkProjectDryRun(this.archive.data,this.themeId).subscribe({
      next: report => {
        this.bulkUpdateService.setLastReport(report);
        this.activeModal.close();
        this.router.navigate(['pages','projet','bulk-update-report'],{queryParams: {"dryRun":"true", "archiveId":report.archiveId}});
      },
      error: err => {
        this.activeModal.close();
        this.modalService.openErrorModal({
          err:  err,
          modalHeader: `Erreur sur l'archive de mise à jour en masse "${this.archive.name}"`
        });
      }
    });
  }

  selectFile(event: any){
    const target: HTMLInputElement = event.currentTarget as HTMLInputElement;
    const files: FileList = target.files;
    if (files.length > 0) {
      this.archive={
        name : files.item(0).name,
        data : files.item(0)
      };
    }
  }
}
