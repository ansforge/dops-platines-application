///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {RouterModule, Routes} from '@angular/router';
import {UIComponent} from './ui.component';
import {ConfirmModalComponent} from './components/confirmModal/confirmModal.component';

const routes: Routes = [
  {
    path: '',
    component: UIComponent,
    children: [
      {
        path: 'confirm',
        component: ConfirmModalComponent,
      },
      {
        path: 'password',
        component: ConfirmModalComponent,
      }
    ],
  },
];


export const routing = RouterModule.forChild(routes);
