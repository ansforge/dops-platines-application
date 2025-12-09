///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {RouterModule, Routes} from '@angular/router';

import {Certificat} from './certificat.component';


const routes: Routes = [
  {
    path: '',
    component: Certificat,
  },
];


export const routing = RouterModule.forChild(routes);
