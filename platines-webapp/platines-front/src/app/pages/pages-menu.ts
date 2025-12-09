///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

const TITRE_ESPACE_GESTION = 'Espace de gestion';
const TITRE_ESPACE_EDITEUR = 'Espace Éditeur';
const TITRE_OFFRE_TEST = 'Offre de tests';

export const MENU_ITEMS: any[] = [{
  title: TITRE_ESPACE_EDITEUR, group: true, elements: [{
    title: 'Mes chaines de confiance', icon: 'ion-link', link: 'chaines/list',
  }, {
    title: 'Mes applications', icon: 'ion-android-playstore', link: 'applications/listApplications',
  }, {
    title: 'Mes sessions de test', icon: 'ion-erlenmeyer-flask', link: 'sessions/listSessions',
  }]
},

  {
    title: TITRE_OFFRE_TEST, group: true, elements: [{
      title: 'Versions', icon: 'ion-document', link: 'versions/listVersions',
    }, {
      title: 'Bibliothèque de tests', icon: 'ion-ios-book', link: 'projet/listProjets',
    }]
  },

];

export const MENU_MANAGER: any[] = [{
  title: TITRE_ESPACE_GESTION, group: true, elements: [{
    title: 'Gestion des ressources', icon: 'ion-ios-paper-outline', link: 'resources/listResources',
  }, {
    title: 'Gestion des utilisateurs', icon: 'ion-person-stalker', link: 'users/listusers',
  }, {
    title: 'Gestion des sessions', icon: 'fa fa-list-ol', link: 'sessions/gestionSession',
  }]
}, {
  title: TITRE_ESPACE_EDITEUR, group: true, elements: [{
    title: 'Chaines de confiance', icon: 'ion-link', link: 'chaines/list',
  }, {
    title: 'Applications Éditeurs', icon: 'ion-android-playstore', link: 'applications/listApplications',
  }, {
    title: 'Sessions de test', icon: 'ion-erlenmeyer-flask', link: 'sessions/listSessions',
  }]
}, {
  title: TITRE_OFFRE_TEST, group: true, elements: [{
    title: 'Thèmes', icon: 'ion-ios-box', link: 'familles/listFamilles',
  }, {
    title: 'Web services', icon: 'ion-ios-folder', link: 'systemes/listSystemes',
  }, {
    title: 'Versions', icon: 'ion-document', link: 'versions/listVersions',
  }, {
    title: 'Bibliothèque de tests', icon: 'ion-ios-book', link: 'projet/listProjets',
  }, {
    title: 'Compatibilité des tests', icon: 'ion-arrow-swap', link: 'compatibilite',
  }]
}];

export const MENU_ADMIN: any[] = [{
  title: TITRE_ESPACE_GESTION, group: true, elements: [{
    title: 'Préférences', icon: 'fa fa-cogs', link: 'preferences',
  }, {
    title: 'Gestion des ressources', icon: 'ion-ios-paper-outline', link: 'resources/listResources',
  }, {
    title: 'Gestion des certificats', icon: 'ion-unlocked', link: 'certificats/listCertificats',
  }, {
    title: 'Gestion des utilisateurs', icon: 'ion-person-stalker', link: 'users/listusers',
  }, {
    title: 'Gestion des sessions', icon: 'fa fa-list-ol', link: 'sessions/gestionSession',
  }]
}, {
  title: TITRE_ESPACE_EDITEUR, group: true, elements: [{
    title: 'Chaines de confiance', icon: 'ion-link', link: 'chaines/list',
  }, {
    title: 'Applications Éditeurs', icon: 'ion-android-playstore', link: 'applications/listApplications',
  }, {
    title: 'Sessions de test', icon: 'ion-erlenmeyer-flask', link: 'sessions/listSessions',
  }]
}, {
  title: TITRE_OFFRE_TEST, group: true, elements: [{
    title: 'Thèmes', icon: 'ion-ios-box', link: 'familles/listFamilles',
  }, {
    title: 'Web services', icon: 'ion-ios-folder', link: 'systemes/listSystemes',
  }, {
    title: 'Versions', icon: 'ion-document', link: 'versions/listVersions',
  }, {
    title: 'Bibliothèque de tests', icon: 'ion-ios-book', link: 'projet/listProjets',
  }, {
    title: 'Compatibilité des tests', icon: 'ion-arrow-swap', link: 'compatibilite',
  }]
}];
