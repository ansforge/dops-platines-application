///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {RouterModule, Routes} from '@angular/router';

import {UserComponent} from './user.component';


const routes: Routes = [
  {
    path: '',
    component: UserComponent,
  },
];


export const routing = RouterModule.forChild(routes);
