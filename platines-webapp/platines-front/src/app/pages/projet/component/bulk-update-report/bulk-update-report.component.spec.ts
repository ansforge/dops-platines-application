///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

/**
 * (c) Copyright 1998-2023, ANS. All rights reserved.
 */
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { AuthenticationService, Profile } from '../../../../services/authentication.service';
import { BulkUpdateService } from '../bulkUpdateService.service';

import { BulkUpdateReportComponent } from './bulk-update-report.component';

describe('BulkUpdateReportComponent', () => {
  let component: BulkUpdateReportComponent;
  let fixture: ComponentFixture<BulkUpdateReportComponent>;
  let paramMap = new Map([
    ['archiveId','test-archive-id']
  ]);
  let mockActivatedRoute = {
    queryParamMap: {
      subscribe: (f) => {
        f(paramMap);
      }
    }
  };
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ HttpClientTestingModule ],
      declarations: [ BulkUpdateReportComponent ],
      providers: [ 
        AuthenticationService,
        BulkUpdateService,
        {provide: ActivatedRoute, useValue: mockActivatedRoute}
      ]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(BulkUpdateReportComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
