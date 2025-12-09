///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {RouterModule, Routes} from '@angular/router';

import {UsersFamillesComponent} from './usersFamilles.component';


const routes: Routes = [
  {
    path: '',
    component: UsersFamillesComponent,
  },
];


export const routing = RouterModule.forChild(routes);
