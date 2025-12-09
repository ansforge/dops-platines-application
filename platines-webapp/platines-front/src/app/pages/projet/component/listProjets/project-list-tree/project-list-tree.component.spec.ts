///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {ComponentFixture, TestBed} from '@angular/core/testing';

import {ProjectListTreeComponent} from './project-list-tree.component';

describe('ProjectListTreeComponent', () => {
  let component: ProjectListTreeComponent;
  let fixture: ComponentFixture<ProjectListTreeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ProjectListTreeComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(ProjectListTreeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
