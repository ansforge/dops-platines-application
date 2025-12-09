///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {RouterModule, Routes} from "@angular/router";
import {SystemesComponent} from "./systemes.component";
import {ListSystemesComponent} from "./components/listSystemes/listSystemes.component";
import {SystemeComponent} from "./components/systeme/systeme.component";
import {AuthRorManager} from "../../services/auth.ror";

const routes: Routes = [
    {
        path: '',
        component: SystemesComponent,
        children: [
            {
              path: 'listSystemes',
              component: ListSystemesComponent,
              canActivate: [AuthRorManager]
            },
            {
              path: 'systeme',
              component: SystemeComponent,
              canActivate: [AuthRorManager]
            }
          ],
    }
];

export const routing = RouterModule.forChild(routes);
