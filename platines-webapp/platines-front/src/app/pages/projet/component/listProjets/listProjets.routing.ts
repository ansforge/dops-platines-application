///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {RouterModule, Routes} from '@angular/router';

import {ListProjetsComponent} from './listProjets.component';

const routes: Routes = [
  {
    path: '',
    component: ListProjetsComponent,
  },
];


export const routing = RouterModule.forChild(routes);
