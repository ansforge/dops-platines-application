///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

export const TABLE_CONSTANT = {
    TABLE_COLUMN_NAME: "Nom",
    TABLE_COLUMN_DESCRIPTION: "Description",
    TABLE_COLUMN_USER: "Éditeur",
    TABLE_COLUMN_ACTIONS: "Actions",
    TABLE_COLUMN_VISIBILITY: "Public",
    TABLE_COLUMN_STATE: "État attendu",
    TABLE_COLUMN_DOWNLOADABLE: "Téléchargeable",
    TABLE_COLUMN_VALIDITY: "Fin de validité",
    TABLE_COLUMN_FAMILY: "Thème",
    TABLE_COLUMN_WS: "Web Service",
    TABLE_COLUMN_VERSION: "Version",
    TABLE_COLUMN_APPLICATION_ROLE: "Rôle de l'application",
    TABLE_COLUMN_APPLICATION: "Application à tester",
    TABLE_COLUMN_STATUS:"Statut",
    TABLE_COLUMN_PROJECT_ROLE: "Instance déployée",
    TABLE_COLUMN_FIRST_NAME: "Prénom",
    TABLE_COLUMN_ENTREPRISE: "Entreprise",
    TABLE_COLUMN_PROFILE: "Profil",
    TABLE_COLUMN_EMAIL: "Email",
    TABLE_COLUMN_CREATION_DATE: "Date de création",
    TABLE_COLUMN_DURATION: "Durée",
    TABLE_COLUMN_TYPE: "Type",
    TABLE_COLUMN_DEFAULT_VALUE: "Valeur par défaut",
    TABLE_COLUMN_DATE_UPLOAD: "Date MAJ",

    TABLE_ICON_VISIBLE: "icon ion-checkmark-circled checked",
    TABLE_ICON_UNVISIBLE: "icon ion-close-circled failed",
    TABLE_ICON_IS_DOWNLOAD: "icon ion-checkmark-circled checked",
    TABLE_ICON_NOT_DOWNLOAD: "icon ion-close-circled failed",
    TABLE_ICON_DETAIL: "icon ion-clipboard",
    TABLE_ICON_CONFIGURATION: "icon ion-gear-b",
    TABLE_ICON_DUPLICATE: "ion-ios-copy-outline",
    TABLE_ICON_DOWNLOAD: "ion-archive",
    TABLE_ICON_ZIP: "fa fa-file-zip-o",
    TABLE_ICON_EXECUTE: "ion-arrow-right-b",

    TABLE_MESSAGE_NO_DATA: "Pas de résultat",
    TABLE_MESSAGE_LOADING: "Chargement"

}

export const ERROR_CONSTANT = {
    ERROR_MESSAGE_TITLE: "Erreur",
    ERROR_MESSAGE_UPDATE_SYSTEM: "Impossible de modifier le WebService",
    ERROR_MESSAGE_CREATE_SYSTEM: "Impossible de créer le WebService",
    ERROR_MESSAGE_GET_SYSTEM: "Impossible d'afficher le WebService",
    ERROR_MESSAGE_ALL_SYSTEMS: "Impossible de charger la liste des WebServices",
    ERROR_MESSAGE_ALL_CERTIFICATES: "Impossible de charger la liste des certificats",
    ERROR_MESSAGE_DELETE_CERTIFICATE: "Un ou plusieurs certificats n'ont pas pu être supprimés",
    ERROR_MESSAGE_ANALYZE_CERTIFICATE: "Fichier ou mot de passe non valide.",
    ERROR_MESSAGE_DELETE_APPLICATION: "Une ou plusieurs applications n'ont pas pu être supprimées",
    ERROR_MESSAGE_GET_CERTIFICATE: "Impossible d'afficher le certificat",
    ERROR_MESSAGE_UPDATE_CERTIFICATE: "Impossible de modifier le certificat.",
    ERROR_MESSAGE_CREATE_CERTIFICATE: "Impossible de créer le certificat.",
    ERROR_MESSAGE_DELETE_FAMILY: "Un ou plusieurs thèmes n'ont pas pu être supprimés.",
    ERROR_MESSAGE_ALL_FAMILIES: "Impossible de charger la liste des thèmes.",
    ERROR_MESSAGE_GET_FAMILY: "Impossible d'afficher le thème.",
    ERROR_MESSAGE_UPDATE_FAMILY: "Impossible de modifier le thème",
    ERROR_MESSAGE_CREATE_FAMILY: "Impossible de créer le thème",
    ERROR_MESSAGE_ALL_VERSIONS: "Impossible de charger la liste des versions.",
    ERROR_MESSAGE_GET_VERSION: "Impossible d'afficher la version.",
    ERROR_MESSAGE_UPDATE_VERSION: "Impossible de modifier la version.",
    ERROR_MESSAGE_CREATE_VERSION: "Impossible de créer la version.",
    ERROR_MESSAGE_CREATE_NOMENCLATURE: "Chargement des nomenclatures échoué.",
    ERROR_MESSAGE_ALL_SESSIONS: "Impossible de charger la liste des versions.",
    ERROR_MESSAGE_DELETE_SESSION: "Une ou plusieurs sessions n'ont pas pu être supprimés.",
    ERROR_MESSAGE_DELETE_PROJECT: "Un ou plusieurs projets n'ont pas pu être supprimés",
    ERROR_MESSAGE_ADD_PROJECT_SESSION: "Vous ne pouvez ajouter qu'un seul projet à la session",
    ERROR_MESSAGE_GET_SESSION: "Impossible de charger la session",
    ERROR_MESSAGE_ALL_USERS: "Impossible de charger la liste des utilisateurs.",
    ERROR_MESSAGE_ALL_APPLICATIONS: "Impossible de charger la liste des applications.",
    ERROR_MESSAGE_ALL_DURATIONS: "Impossible de charger la liste des durées de session.",
    ERROR_MESSAGE_GET_APPLICATION: "Impossible de charger l'application",
    ERROR_MESSAGE_ALL_PROJECTS: "Impossible de charger la liste des projets.",
    ERROR_MESSAGE_UPDATE_SESSION: "Impossible de modifier la session.",
    ERROR_MESSAGE_CREATE_SESSION: "Impossible de créer la session.",
    ERROR_MESSAGE_GET_LOGS: "Impossible de charger les logs.",
    ERROR_MESSAGE_ALL_RELATED_FILES: "Impossible de charger la liste des fichiers associés.",
    ERROR_MESSAGE_GET_ZIP: "Erreur lors du téléchargement du zip",
    ERROR_MESSAGE_GET_FILE: "Erreur lors du téléchargement du fichier associé",
}

export const SUCCESS_CONSTANT = {
    SUCCESS_TITLE_NOMENCLATURE: "Chargement Nomenclatures",
    SUCCESS_CREATE_NOMENCLATURE: "Chargement des nomenclatures réussi",
    SUCCESS_TITLE_SESSION: "Ajout de session",
    SUCCESS_CREATE_SESSION: "La session a bien été créée"
}

export const MODAL_CONSTANT = {
    MODAL_HEADER_PASSWORD: "Mot de passe du certificat",
    MODAL_CONTENT_PASSWORD: "Mot de passe : ",
    MODAL_HEADER_DELETE_FAMILY: "Suppression de thème",
    MODAL_CONTENT_DELETE_FAMILY: "Êtes-vous sûr de vouloir supprimer le ou les thème(s) sélectionné(s) ?",
    MODAL_HEADER_DELETE_CERTIF: "Suppression de certificat",
    MODAL_CONTENT_DELETE_CERTIF: "Êtes-vous sûr de vouloir supprimer le ou les certificat(s) sélectionné(s) ?",
    MODAL_HEADER_DELETE_SESSION: "Suppression d'une session de test",
    MODAL_CONTENT_DELETE_SESSION: "Êtes-vous sûr de vouloir supprimer la ou les session(s) sélectionnée(s) ?",
    MODAL_CONTENT_SUPPRESS_SESSION: "Êtes-vous sûr de vouloir supprimer définitivement une ou plusieurs sessions de la base de données ?",
    MODAL_HEADER_RESET_MDP: "Reset du mot de pass"

}

export const TABLE_TEXT = {
    noData: "Pas de données enregistrées",
    noMatchingData: "Pas de resultat correspondant",
    loading: "Chargement"
}

export const TABLE_BUTTON_TITLE = {
    BUTTON_AFFECT_USERS: "Affecter des ustilisateurs",
    BUTTON_DOWNLOAD_CERTIFICATE: "Télécharger le certificat associé au projet",
    BUTTON_DOWNLOAD_PROJECT: "Télécharger le projet",
    BUTTON_DETAIL_PROJECT: "Voir les détails du projet",
    BUTTON_CONFIGURE_PROJECT: "Modifier le projet",
    BUTTON_DETAIL_SESSION: "Détails de l'exécution",
    BUTTON_DUPLICATE_SESSION: "Dupliquer la session",
    BUTTON_EDIT_SESSION: "Modifier la session",
    BUTTON_EXECUTE_SESSION: "Exécuter session",
    BUTTON_CONFIGURE_APPLICATION: "Modifier l'application",
    BUTTON_CONFIGURE_TEST_CERTIFICAT: "Modifier le certificat",
    BUTTON_CONFIGURE_FAMILY: "Modifier le thème",
    BUTTON_CONFIGURE_SYSTEM: "Modifier le web-service",
    BUTTON_CONFIGURE_VERSION: "Modifier la version",
}
