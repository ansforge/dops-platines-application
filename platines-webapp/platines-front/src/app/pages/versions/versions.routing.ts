///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {RouterModule, Routes} from '@angular/router';
import {VersionsComponent} from './versions.component';
import {ListVersionsComponent} from './components/listVersions/listVersions.component';
import {VersionComponent} from './components/version/version.component';
import {AuthRor, AuthRorManager} from '../../services/auth.ror';


const routes: Routes = [
  {
    path: '',
    component: VersionsComponent,
    children: [
      {
        path: 'version',
        component: VersionComponent,
        canActivate: [AuthRorManager]
      },
      {
        path: 'listVersions',
        component: ListVersionsComponent,
        canActivate: [AuthRor]
      },
    ],
  },
];


export const routing = RouterModule.forChild(routes);
