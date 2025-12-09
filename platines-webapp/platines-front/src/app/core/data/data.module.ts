///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {ModuleWithProviders, NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';

import {StateService} from './state.service';

const SERVICES = [
  StateService,
];

@NgModule({
  imports: [
    CommonModule,
  ],
  providers: [
    ...SERVICES,
  ],
})
export class DataModule {
  static forRoot(): ModuleWithProviders<any> {
    return <ModuleWithProviders<any>>{
      ngModule: DataModule,
      providers: [
        ...SERVICES,
      ],
    };
  }
}
