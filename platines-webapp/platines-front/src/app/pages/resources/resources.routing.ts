///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {RouterModule, Routes} from '@angular/router';

import {ListResourcesComponent} from './components/listResources/listResources.component';
import {AuthRorManager} from '../../services/auth.ror';
import {ResourcesComponent} from './resources.component';


const routes: Routes = [
  {
    path: '',
    component: ResourcesComponent,
    children: [
      {
        path: 'listResources',
        component: ListResourcesComponent,
        canActivate: [AuthRorManager]
      },
    ],
  },
];


export const routing = RouterModule.forChild(routes);
