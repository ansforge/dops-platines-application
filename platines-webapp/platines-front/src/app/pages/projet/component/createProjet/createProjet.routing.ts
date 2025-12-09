///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {RouterModule, Routes} from '@angular/router';

import {CreateProjetComponent} from './createProjet.component';


const routes: Routes = [
  {
    path: '',
    component: CreateProjetComponent,
  },
];


export const routing = RouterModule.forChild(routes);
