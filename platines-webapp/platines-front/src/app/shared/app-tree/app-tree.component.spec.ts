///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

/**
 * (c) Copyright 1998-2023, ANS. All rights reserved.
 */
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AppTranslationModule } from '../../app.translation.module';

import { AppTreeComponent } from './app-tree.component';

describe('AppTreeComponent', () => {
  let component: AppTreeComponent<any, any>;
  let fixture: ComponentFixture<AppTreeComponent<any, any>>;
  
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ 
        AppTranslationModule,
        HttpClientTestingModule
      ],
      declarations: [ 
        AppTreeComponent
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AppTreeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
