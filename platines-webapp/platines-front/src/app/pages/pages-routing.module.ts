///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

import {RouterModule, Routes} from '@angular/router';
import {PagesComponent} from './pages.component';
import {ModuleWithProviders} from '@angular/core';
import {AuthRor, AuthRorAdmin, AuthRorManager, AuthRorAccueil} from '../services/auth.ror';

const routes: Routes = [
  {
    path: '',
    component: PagesComponent,
    children: [
      {
        path: 'login',
        loadChildren: () => import('app/pages/login/login.module').then(m => m.LoginModule),
      }, {
        path: 'forgot',
        loadChildren: () => import('app/pages/forgot/forgot.module').then(m => m.ForgotModule),
      }, {
        path: 'reset',
        loadChildren: () => import('app/pages/reset/reset.module').then(m => m.ResetModule),
      },
      {
        path: '',
        redirectTo: 'login',
        pathMatch: 'full'
      },
    ]
  },
  {
    path: 'pages',
    component: PagesComponent,
    children: [
      {
        path: 'profile',
        loadChildren: () => import('./profile/profile.module').then(m => m.ProfileModule),
        canActivate: [AuthRor]
      },
      {
        path: 'compatibilite',
        loadChildren: () => import('./compatibilite/compatibilite.module').then(m => m.CompatibiliteModule),
        canActivate: [AuthRorManager]
      },
      {
        path: 'preferences',
        loadChildren: () => import('./preferences/preferences.module').then(m => m.PreferencesModule),
        canActivate: [AuthRorAdmin]
      },
      {
        path: 'projet',
        loadChildren: () => import('./projet/projet.module').then(m => m.ProjetModule),
        canActivate: [AuthRor]
      },
      {
        path: 'versions',
        loadChildren: () => import('./versions/versions.module').then(m => m.VersionsModule),
        canActivate: [AuthRor]
      },
      {
        path: 'systemes',
        loadChildren: () => import('./systemes/systemes.module').then(m => m.SystemesModule),
        canActivate: [AuthRorManager]
      },
      {
        path: 'applications',
        loadChildren: () => import('./applications/applications.module').then(m => m.ApplicationsModule),
        canActivate: [AuthRor]
      },
      {
        path: 'sessions',
        loadChildren: () => import('./sessions/sessions.module').then(m => m.SessionsModule),
        canActivate: [AuthRor]
      },
      {
        path: 'chaines',
        loadChildren: () => import('./chaines/chaines.module').then(m => m.ChainesModule),
        canActivate: [AuthRor]
      },
      {
        path: 'users', loadChildren: () => import('./users/users.module').then(m => m.UsersModule),
        canActivate: [AuthRorManager]
      },
      {
        path: 'certificats',
        loadChildren: () => import('./certificats/certificats.module').then(m => m.CertificatsModule),
        canActivate: [AuthRor]
      },
      {
        path: 'resources', loadChildren: () => import('./resources/resources.module').then(m => m.ResourcesModule),
        canActivate: [AuthRorManager]
      },
      {
        path: 'accueil', 
        loadChildren: () => import('./accueil/accueil.module').then(m => m.AccueilModule),
        canActivate: [AuthRorAccueil]
      },
      {
        path: 'familles',
        loadChildren: () => import('./familles/familles.module').then(m => m.FamillesModule),
        canActivate: [AuthRor]
      },
      {path: '', redirectTo: 'accueil', pathMatch: 'full'}],


  }];
export const routing: ModuleWithProviders<any> = RouterModule.forChild(routes);
