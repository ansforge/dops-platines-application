///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {RouterModule, Routes} from '@angular/router';

import {CertificatsComponent} from './certificats.component';
import {CertificatComponent} from './components/certificat/certificat.component';
import {ListCertificatsComponent} from './components/listCertificats/listCertificats.component';
import {AuthRorAdmin} from '../../services/auth.ror';

const routes: Routes = [
  {
    path: '',
    component: CertificatsComponent,
    children: [
      {
        path: 'certificat',
        component: CertificatComponent,
        canActivate: [AuthRorAdmin]
      },
      {
        path: 'listCertificats',
        component: ListCertificatsComponent,
        canActivate: [AuthRorAdmin]
      },
    ],
  },
];


export const routing = RouterModule.forChild(routes);
