///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {RouterModule, Routes} from '@angular/router';

import {ProfileComponent} from './profile.component';
import {AuthRor} from '../../services/auth.ror';

const routes: Routes = [
  {
    path: '',
    component: ProfileComponent,
    canActivate: [AuthRor]
  },
];


export const routing = RouterModule.forChild(routes);
