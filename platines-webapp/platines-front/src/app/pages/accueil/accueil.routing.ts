///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {RouterModule, Routes} from '@angular/router';

import {AccueilComponent} from './accueil.component';

const routes: Routes = [
  {
    path: '',
    component: AccueilComponent,
  },
];


export const routing = RouterModule.forChild(routes);
