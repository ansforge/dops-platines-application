///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {RouterModule, Routes} from '@angular/router';
import {SessionComponent} from './session.component';

const routes: Routes = [
  {
    path: '',
    component: SessionComponent,
  },
];

export const routing = RouterModule.forChild(routes);
