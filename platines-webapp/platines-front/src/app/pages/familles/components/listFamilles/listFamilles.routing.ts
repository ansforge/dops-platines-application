///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {RouterModule, Routes} from '@angular/router';

import {ListFamillesComponent} from './listFamilles.component';


const routes: Routes = [
  {
    path: '',
    component: ListFamillesComponent,
  },
];


export const routing = RouterModule.forChild(routes);
