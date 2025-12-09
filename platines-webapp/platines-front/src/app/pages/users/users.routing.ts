///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {RouterModule, Routes} from '@angular/router';

import {UsersComponent} from './users.component';
import {ListUsersComponent} from './components/users/listusers.component';
import {UserComponent} from './components/user/user.component';
import {AuthRorManager} from '../../services/auth.ror';

const routes: Routes = [
  {
    path: '',
    component: UsersComponent,
    children: [
      { path: 'listusers', component: ListUsersComponent, canActivate: [AuthRorManager] },
      { path: 'user', component: UserComponent, canActivate: [AuthRorManager] },
   ]
  },
];


export const routing = RouterModule.forChild(routes);
