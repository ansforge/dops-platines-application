///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {RouterModule, Routes} from '@angular/router';

import {FamilleComponent} from './famille.component';


const routes: Routes = [
  {
    path: '',
    component: FamilleComponent,
  },
];


export const routing = RouterModule.forChild(routes);
