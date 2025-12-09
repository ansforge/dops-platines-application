///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {RouterModule, Routes} from '@angular/router';
import {SessionServeurComponent} from './sessionServeur.component';

const routes: Routes = [
  {
    path: '',
    component: SessionServeurComponent,
  },
];

export const routing = RouterModule.forChild(routes);
