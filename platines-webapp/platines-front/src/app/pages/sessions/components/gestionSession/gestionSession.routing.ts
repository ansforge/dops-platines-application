///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {RouterModule, Routes} from '@angular/router';
import {GestionSessionComponent} from './gestionSession.component';

const routes: Routes = [
  {
    path: '',
    component: GestionSessionComponent,
  },
];

export const routing = RouterModule.forChild(routes);
