///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {RouterModule, Routes} from '@angular/router';

import {ApplicationComponent} from './application.component';


const routes: Routes = [
  {
    path: '',
    component: ApplicationComponent,
  },
];


export const routing = RouterModule.forChild(routes);
