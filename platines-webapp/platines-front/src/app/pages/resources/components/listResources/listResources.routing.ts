///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {RouterModule, Routes} from '@angular/router';

import {ListResourcesComponent} from './listResources.component';


const routes: Routes = [
  {
    path: '',
    component: ListResourcesComponent,
  },
];


export const routing = RouterModule.forChild(routes);
