///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {RouterModule, Routes} from '@angular/router';

import {Chaines} from './chaines.component';
import {Listchaines} from './components/listchaines/listchaines.component';
import {Chaine} from './components/chaine/chaine.component';
import {Certificat} from './components/certificat/certificat.component';
import {AuthRor} from '../../services/auth.ror';
// noinspection TypeScriptValidateTypes
const routes: Routes = [
  {
    path: '',
    component: Chaines,
    children: [
       { path: 'list', component: Listchaines, canActivate: [AuthRor] },
       { path: 'chaine', component: Chaine, canActivate: [AuthRor] },
       { path: 'certificat', component: Certificat, canActivate: [AuthRor] },
    ]
  },
];

export const routing = RouterModule.forChild(routes);
