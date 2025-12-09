///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {RouterModule, Routes} from '@angular/router';

import {ListApplicationsComponent} from './listApplications.component';


const routes: Routes = [
  {
    path: '',
    component: ListApplicationsComponent,
  },
];


export const routing = RouterModule.forChild(routes);
