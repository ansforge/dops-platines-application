///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

/**
 * (c) Copyright 1998-2023, ANS. All rights reserved.
 */
import {RouterModule, Routes} from '@angular/router';

import {ProjetComponent} from './projet.component';
import {ProjetDetailComponent} from './component/projetDetail/projetDetail.component';
import {ListProjetsComponent} from './component/listProjets/listProjets.component';
import {CreateProjetComponent} from './component/createProjet/createProjet.component';

import {AuthRor, AuthRorManager} from '../../services/auth.ror';
import { BulkUpdateReportComponent } from './component/bulk-update-report/bulk-update-report.component';

const routes: Routes = [
  {
    path: '',
    component: ProjetComponent,
    children: [
      { path: 'projetDetail/:id', component: ProjetDetailComponent, canActivate: [AuthRor] },
      { path: 'listProjets', component: ListProjetsComponent, canActivate: [AuthRor] },
      { path: 'create', component: CreateProjetComponent, canActivate: [AuthRorManager] },
      { path: 'bulk-update-report', component: BulkUpdateReportComponent , canActivate: [AuthRorManager] }
    ],
  },
];


export const routing = RouterModule.forChild(routes);
