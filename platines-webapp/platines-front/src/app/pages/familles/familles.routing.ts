///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {RouterModule, Routes} from '@angular/router';

import {FamillesComponent} from './familles.component';
import {FamilleComponent} from './components/famille/famille.component';
import {ListFamillesComponent} from './components/listFamilles/listFamilles.component';
import {AuthRorManager} from '../../services/auth.ror';
import {UsersFamillesComponent} from './components/usersFamilles/usersFamilles.component';

const routes: Routes = [
  {
    path: '',
    component: FamillesComponent,
    children: [
      {
        path: 'famille',
        component: FamilleComponent,
        canActivate: [AuthRorManager]
      },
      {
        path: 'listFamilles',
        component: ListFamillesComponent,
        canActivate: [AuthRorManager]
      },
      {
        path: 'usersFamilles',
        component: UsersFamillesComponent,
        canActivate: [AuthRorManager]
      },
    ],
  },
];


export const routing = RouterModule.forChild(routes);
