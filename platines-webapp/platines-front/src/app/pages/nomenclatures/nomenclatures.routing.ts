///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {RouterModule, Routes} from '@angular/router';

import {NomenclaturesComponent} from './nomenclatures.component';
import {AuthRorManager} from '../../services/auth.ror';


const routes: Routes = [
  {
    path: '',
    component: NomenclaturesComponent,
    canActivate: [AuthRorManager]
  },
];


export const routing = RouterModule.forChild(routes);
