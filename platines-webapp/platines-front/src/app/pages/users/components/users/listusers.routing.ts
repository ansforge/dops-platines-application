///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {RouterModule, Routes} from '@angular/router';

import {ListUsersComponent} from './listusers.component';


const routes: Routes = [
  {
    path: '',
    component: ListUsersComponent,
  },
];


export const routing = RouterModule.forChild(routes);
