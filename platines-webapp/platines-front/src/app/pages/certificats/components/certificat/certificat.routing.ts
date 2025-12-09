///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {RouterModule, Routes} from '@angular/router';

import {CertificatComponent} from './certificat.component';


const routes: Routes = [
  {
    path: '',
    component: CertificatComponent,
  },
];


export const routing = RouterModule.forChild(routes);
