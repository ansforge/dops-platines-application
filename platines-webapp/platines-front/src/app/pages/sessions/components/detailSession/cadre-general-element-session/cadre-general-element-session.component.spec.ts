///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SessionService } from '../../../../../services/session.service';
import { AppTranslationModule } from '../../../../../app.translation.module';
import { FileService } from '../../../../../services/file.service';
import { ProjectLibraryService } from '../../../../../services/projectlibrary.service';

import { CadreGeneralElementSessionComponent } from './cadre-general-element-session.component';
import { ProfileService } from '../../../../../services/profile.service';
import { AuthenticationService } from '../../../../../services/authentication.service';
import { Observable } from 'rxjs';
import { SessionDetail } from '../../../../../pages/entity/sessiondetail';
import { SessionResultTreeNode } from '../session-tree.model';

describe('CadreGeneralElementSessionComponent', () => {
  let component: CadreGeneralElementSessionComponent;
  let fixture: ComponentFixture<CadreGeneralElementSessionComponent>;
  let mockSessionInput = new Observable<SessionDetail>();
  let mockNodeSelection = new Observable<SessionResultTreeNode>();
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ 
        HttpClientTestingModule,
        AppTranslationModule
      ],
      declarations: [ CadreGeneralElementSessionComponent ],
      providers: [ 
        ProjectLibraryService,
        FileService,
        SessionService,
        ProfileService,
        AuthenticationService
       ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CadreGeneralElementSessionComponent);
    fixture.componentInstance.sessionDetails = mockSessionInput;
    fixture.componentInstance.nodeSelection = mockNodeSelection;
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
