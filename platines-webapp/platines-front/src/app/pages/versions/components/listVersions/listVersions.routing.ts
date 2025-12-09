///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {RouterModule, Routes} from '@angular/router';

import {ListVersionsComponent} from './listVersions.component';


const routes: Routes = [
  {
    path: '',
    component: ListVersionsComponent,
  },
];


export const routing = RouterModule.forChild(routes);
