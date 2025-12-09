///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {RouterModule, Routes} from '@angular/router';

import {Listchaines} from './listchaines.component';


const routes: Routes = [
  {
    path: '',
    component: Listchaines,
  },
];


export const routing = RouterModule.forChild(routes);
