///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {RouterModule, Routes} from '@angular/router';
import {DetailSessionComponent} from './detailSession.component';

const routes: Routes = [
  {
    path: '',
    component: DetailSessionComponent,
  },
];

export const routing = RouterModule.forChild(routes);
