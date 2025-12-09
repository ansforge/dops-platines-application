///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {RouterModule, Routes} from '@angular/router';

import {VersionComponent} from './version.component';


const routes: Routes = [
  {
    path: '',
    component: VersionComponent,
  },
];


export const routing = RouterModule.forChild(routes);
