///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SessionResultTreeComponent } from './session-result-tree.component';

describe('SessionResultTreeComponent', () => {
  let component: SessionResultTreeComponent;
  let fixture: ComponentFixture<SessionResultTreeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SessionResultTreeComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SessionResultTreeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
