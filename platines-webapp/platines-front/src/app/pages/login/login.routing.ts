///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {RouterModule, Routes} from '@angular/router';

import {LoginComponent} from './login.component';
import {ModuleWithProviders} from '@angular/core';

// noinspection TypeScriptValidateTypes
export const routes: Routes = [
  {
    path: '',
    component: LoginComponent
  }
];

export const routing: ModuleWithProviders<any> = RouterModule.forChild(routes);
