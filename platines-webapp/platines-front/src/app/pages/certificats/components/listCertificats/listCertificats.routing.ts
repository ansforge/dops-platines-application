///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {RouterModule, Routes} from '@angular/router';

import {ListCertificatsComponent} from './listCertificats.component';


const routes: Routes = [
  {
    path: '',
    component: ListCertificatsComponent,
  },
];


export const routing = RouterModule.forChild(routes);
