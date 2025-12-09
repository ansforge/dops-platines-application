///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {RouterModule, Routes} from '@angular/router';

import {Reset} from './reset.component';
import {ModuleWithProviders} from '@angular/core';

// noinspection TypeScriptValidateTypes
export const routes: Routes = [
  {
    path: 'reset',
    component: Reset
  }
];

export const routing: ModuleWithProviders<any> = RouterModule.forChild(routes);
