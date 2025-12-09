///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {RouterModule, Routes} from '@angular/router';
import {ListSessionsComponent} from './listSessions.component';

const routes: Routes = [
  {
    path: '',
    component: ListSessionsComponent,
  },
];

export const routing = RouterModule.forChild(routes);
