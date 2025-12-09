///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {RouterModule, Routes} from '@angular/router';

import {PreferencesComponent} from './preferences.component';
import {AuthRorAdmin} from '../../services/auth.ror';


const routes: Routes = [
  {
    path: '',
    component: PreferencesComponent,
    canActivate: [AuthRorAdmin]
  },
];


export const routing = RouterModule.forChild(routes);
