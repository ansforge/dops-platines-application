///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {RouterModule, Routes} from '@angular/router';

import {ApplicationsComponent} from './applications.component';
import {ApplicationComponent} from './components/application/application.component';
import {ListApplicationsComponent} from './components/listApplications/listApplications.component';
import {AuthRor} from '../../services/auth.ror';

const routes: Routes = [
  {
    path: '',
    component: ApplicationsComponent,
    children: [
      {
        path: 'application',
        component: ApplicationComponent,
        canActivate: [AuthRor]
      },
      {
        path: 'listApplications',
        component: ListApplicationsComponent,
        canActivate: [AuthRor]
      },
    ],
  },
];


export const routing = RouterModule.forChild(routes);
