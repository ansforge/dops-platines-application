///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

/**
 * (c) Copyright 1998-2023, ANS. All rights reserved.
 */
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { AppTranslationModule } from './../../../../../app.translation.module';
import { FileService } from '../../../../../services/file.service';
import { ProjectLibraryService } from '../../../../../services/projectlibrary.service';
import { BulkUpdateService } from '../../bulkUpdateService.service';

import { BulkUpdateChoiceboxComponent } from './bulk-update-choicebox.component';
import { ActivatedRoute } from '@angular/router';

describe('BulkUpdateChoiceboxComponent', () => {
  let component: BulkUpdateChoiceboxComponent;
  let fixture: ComponentFixture<BulkUpdateChoiceboxComponent>;
  let mockActivatedRoute = {};
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ HttpClientTestingModule, AppTranslationModule ],
      declarations: [ BulkUpdateChoiceboxComponent ],
      providers: [
        NgbActiveModal,
        BulkUpdateService,
        FileService,
        ProjectLibraryService,
        {provide: ActivatedRoute, useValue: mockActivatedRoute}
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BulkUpdateChoiceboxComponent);
    component = fixture.componentInstance;
   /*
    * Cf si nécessaire pour tests plus avancés.
    * let httpMock=fixture.debugElement.injector.get(HttpTestingController);
    */
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
