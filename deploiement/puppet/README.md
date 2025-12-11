# platines

#### Table des matières 

1. [Vue globale](#vue-globale)
2. [Description du module - Ce que le module doit faire](#description)
3. [Configuration](#configuration)
  * 3.1 [Dépendance](#Dépendance)
  * 3.2 [Ressources](#Ressources)
  * 3.3 [Variables/Paramètres](#variables/paramètres)
  * 3.4 [Installation](#installation)
  * 3.5 [Limitations](#limitations)
4. [Développement](#developpement)


## 1. Vue globale

Installation et lancement du job platines.nomad

## 2. Description

Module permettant d'installer et de lancer le job platines en utilisant un template pour la configuration spécifique du job.

Actions globales:

Le module comporte 4 fichiers :
	- install.pp : Installation du job et mise à jour des paramètres par le template
	- deploy.pp : Lancement du job
	- init.pp : Ordonnancement des fichiers .pp
	- param.pp : Fichier de paramétrage

## 3. Configuration:

Les paramètres sont à consulter dans le DIA associé au module Puppet : il est récupérable auprès de l'ASIP Santé.

#### 3.1 Dépendances

```json
{"name": "puppetlabs/pe_gem", "version_requirement": ">= 0.0.1"},
{"name": "puppet/archive", "version_requirement": ">= 2.0.0 < 5.0.0"}
```

#### 3.2 Ressources



#### 3.3 Variables/Paramètres
  
  ### Paramètres dans le fichier environnement.yaml
  ##Paramètres de TYPE III
  
  ###############################
  # Artifactory parameters      #
  ###############################
  url_artifactory : url du serveur de stockage des binaires, Artifactory, composé du protocole et du nom de domaine
  
  ###############################
  # Platines template parameters#
  ###############################
  
  $platines_mysql_user : utilisateur de la base mysql
  $platines_mysql_database : nom de la base mysql de l'utilisateur $platines_mysql_user
  $platines_public_hostname : nom public de la plateforme enregistré dans les DNS publics.
  $platines_hostname_haproxy : hostname de la machine sur laquelle s'installe haproxy
  $platines_mock_service_domain : Nom de domaine réservé au déploiement des mockservices 
  $platines_distrib_version : Numéro de version de l'application
  $platines_front_distrib_version : Numéro de version du front de l'application
  $platines_mock_distrib_version : Numéro de version des mock de l'application
  
  ##Paramètres de TYPE II
  
  ###############################
  # Template parameters         #
  ###############################
  
  $https_proxy : URL du proxy de sortie par https, valeur exemple : "https://10.0.49.163:3128" ''
  $http_proxy : URL du proxy de sortie par http, valeur exemple : "http://10.0.49.163:3128" ''
  $smtp_host : nom du serveur smtp pour les mails sortants 'localhost'
  $smtp_port : port du serveur SMTP des mails sortants '25'
  $smtp_proxy_host : nom du proxy smtp ''
  $smtp_proxy_port : port du proxy SMTP des mails sortants ''
  $mariadb_image_rhel: Numéro d’image docker pour MariaDB 'latest'
  $logstash_host = adresse du serveur logstash sous la forme "X.X.X.X:port" (pas de valeur par défaut)
 
  ###############################
  # Platines template parameters#
  ###############################
  
  $platines_size_memory_jvm : mémoire accordée aux JVMs de Tomcat '256M'
  $platines_version_nomad_tmpl : numéro de version du template nomad de configuration de haproxy, du template de job mock.nomad.json et du template pour le job client 'latest'
  $platines_repo : dépôt artifactory qui héberge l'application 'repo-releases'
  $platines_dbserver_mem_size : taille du conteneur docker sur le serveur de base de donnée '1024'
  $platines_appserver_mem_size : taille du conteneur docker sur le serveur applicatif '3072'
  $platines_rpserver_mem_size : taille du conteneur docker sur le reverse proxy '1024'
  $platines_job_name : nom du job Nomad 'platines'
  $platines_java_initial_mem_ratio = java_initial_mem_ratio '100'
  $platines_java_max_mem_ratio = java_max_mem_ratio '80'
  $platines_gc_max_metaspace_size = gc_max_metaspace_size '256'
  
  
  ### Paramètres dans le fichier certificat.yaml
  N/A : Les certificats sont gérés par Vault

#### 3.4 Installation

L'installation de platines est définie dans le DIA.

#### 3.5 Limitations

Le déploiement Puppet est testé pour une version RHEL 7.5 ou supérieure en architecture 64 bits et Puppet 6.

## 4. Développement

Contacter l'A.N.S.
