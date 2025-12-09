///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {RouterModule, Routes} from '@angular/router';
import {SessionComponent} from './components/session/session.component';
import {ListSessionsComponent} from './components/listSessions/listSessions.component';
import {DetailSessionComponent} from './components/detailSession/detailSession.component';
import {SessionsComponent} from './sessions.component';
import {SessionServeurComponent} from './components/sessionServeur/sessionServeur.component';
import {GestionSessionComponent} from './components/gestionSession/gestionSession.component';

import {AuthRor, AuthRorManager} from '../../services/auth.ror';

const routes: Routes = [
  {
    path: '',
    component: SessionsComponent,
    children: [
      {
        path: 'session',
        component: SessionComponent,
        canActivate: [AuthRor]
      },
      {
        path: 'listSessions',
        component: ListSessionsComponent,
        canActivate: [AuthRor]
      },
      {
        path: 'details',
        component: DetailSessionComponent,
        canActivate: [AuthRor]
      },
      {
        path: 'sessionServeur',
        component: SessionServeurComponent,
        canActivate: [AuthRor]
      },
      {
        path: 'gestionSession',
        component: GestionSessionComponent,
        canActivate: [AuthRorManager]
      },
    ],
  },
];


export const routing = RouterModule.forChild(routes);
