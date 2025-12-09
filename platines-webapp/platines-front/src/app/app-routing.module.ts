///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {RouterModule, Routes} from '@angular/router';
import {ModuleWithProviders} from '@angular/core';

const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
];

export const routing: ModuleWithProviders<any> = RouterModule.forRoot(routes, { useHash: true, enableTracing: false });
