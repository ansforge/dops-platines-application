///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {RouterModule, Routes} from '@angular/router';

import {ListSystemesComponent} from './listSystemes.component';


const routes: Routes = [
  {
    path: '',
    component: ListSystemesComponent,
  },
];


export const routing = RouterModule.forChild(routes);
