## 30-06-2017 - Release 0.1.0

#### Bugfixes
- n/a

#### Création du module


## 26-10-2017 - Release 0.1.1

#### Bugfixes
- n/a

#### Ajout de variable dans le template
#### Modification du paramètre pour le certificat serveur
#### Ajout du proxy smtp et test du job avant exécution
#### Suppression des tabulations dans le template certificat


## 09-11-2017 - Release 0.2.0

#### Bugfixes
- n/a

#### Changement du template du job nomad (ajout des variables server nomad et consul)
#### Ajout de la variable "size_memory_jvm"
#### Ajout de la variable "job_mock_service_tmpl" + d'un artifact téléchargeant mock.nomad.json dans tmp
#### Ajout des variables secure_protocol_api, webapps_folder, consul_host + ajout des tags "asipsante.platines" au niveau des services
#### Changement de l'emplacement de téléchargement du template mock.json
#### Changement du template du job nomad (ajout d'une contrainte pour fixer l'emplacement de haproxy sur une VM)
#### Mise à jour du README
#### Mise à jour des commentaires des classes
#### Changement de la version dans le metadata
#### Ajout de paramètres pour variabiliser les ports externe de haproxy
#### Corrections syntaxiques
#### Ajout d'un artifact dans la tâche tomcat pour télécharger web.xml dans local/mocks/WEB-INF/
#### Suppression des variables inutiles (CONSUL_SERVER et NOMAD_SERVER)
#### Mise à jour du template du job nomad et du module pour variabiliser l'emplacement de web.xml et mock.nomad.json sur le repository
#### Ajout de la variable MOCK_SERVICE_DOMAIN et NOMAD_API_PORT

## 16-11-2017 - Release 0.2.1

#### Bugfixes
- n/a

#### Mise à jour de la version
#### Mise à jour du README

### 17-11-2017 - Release 0.2.2

#### Bugfixes
- n/a

#### Ajout du port ha_mock (8444) dans network et port map
#### Mise à jour de la version
#### Mise à jour du README
#### Corrections syntaxiques

### 24-11-2017 - Release 0.2.3

#### Bugfixes
- n/a

#### Ajout d'une option pour la commande "plan"
#### Mise à jour de la version dans le metadata
#### Mise à jour du README

### 28-11-2017 - Release 0.2.4

#### Bugfixes
- n/a

#### Modification du chemin artifactory pour l'image docker tomcat dans le template du job nomad
#### Ajout d'une variable pour créer le répertoire de travail pour le build des wars du mock service.
#### Mise à jour de la version dans le metadata
#### Suppression du paramètre MOCK_SERVICE_DOMAIN dans la partie haproxy du template nomad

### 07-12-2017 - Release 0.2.5

#### Bugfixes
- n/a

#### Modification des paramètres de versionning de l'applications et des templates
#### Mise à jour de la version dans le metadata
#### Ajouts de nouveaux paramètres pour la taille des conteneurs docker
#### Mise à jour de variables (passage en global, changement de nom pour plus de cohérence)
#### changement de la valeur par défaut du paramètre platines_size_memory_jvm à '256M'

### 28-12-2017 - Release 0.2.6

#### Bugfixes
#### Ajout du répertoire de travail dans la trace de l'exécution nomad

### 29-12-2017 - Release 0.2.7

#### Bugfixes
- n/a
#### Amélioration de la gestion de la sortie de nomad plan

### 18-01-2018 - Release 0.2.8

#### Bugfixes
- n/a
#### Suppression de l'exécution de mock.nomad.json lors du déploiement : l'exécution se ferait désormais mais à chaque changement du fichier)
#### Ajout d'une nouvelle variable pour l'exécution du jar permettant de créer des sessions clientes dans des images dockers (JOB_TMPL_REPOSITORY)
#### Modification du job nomad pour ajouter une fonction de vérification de redémarrage du service mariaDB

### 27-02-2018 - Release 0.2.9

#### Bugfixes
- n/a
#### Correction sur le nom de la variable "platines_version_nomad_tmpl"

### 14-03-2018 - Release 0.2.10

#### Bugfixes
- n/a
### Variabilisation des noms de services enregistrés dans Consul
### Ajout de la possibilité de chosir le nom du job (paramètre facultatif)

### 22-03-2018 - Release 0.2.11

#### Bugfixes
- n/a
### Ajout d'une contrainte dans le template nomad pour fixer la VM où sera installée la BDD.
### Changement du point de montage du volume Docker pour les données de la BDD
### Nom du template nomad positionné en dur

### 06-04-2018
### Mise à jour du template : 
    + utilisation d'images docker natives pour mariaDB et Tomcat (ajout de variables)
    + suppression d'ephemeral disk pour mariaDB 
    + changement des répertoires temporaires des images pour tomcat 
    + ajout d'un fichier de configuration tomcat récupéré sur Artifactory 
    + MAJ de paramètre pour le conteneur tomcat 

### Changement du chemin vers les binaires de l'application dans Artifactory ($repo_appli_path)

### 25-07-2018
### Mise à jour du template :
	+ ajout du group front-servers-mock pour disposer d'un haproxy pour l'application et d'un autre pour les mockservices
	
###	16/05/2019
###	Mise à jour du template :
	+ ajout de la variable GC_MAX_METASPACE_SIZE dans le group "apps-servers"
	+ variabilisation des paramètres JAVA_INITIAL_MEM_RATIO, JAVA_MAX_MEM_RATIO et GC_MAX_METASPACE_SIZE dans le group "apps-servers"

### 19-07-2019 - Release 0.2.15

### 19/07/2019
### Mise à jour des paramètres :
    + Chemin de téléchargement des artifacts platines (refactoring des projets maven)
	
### 10/10/2019 - Release 0.2.16
    + Ajout d'une variable d'environnement pour la gestion des logs proxy-mock
    

### 10-01-2020 - Release 6.0.0
    + Migration en Puppet 6.
	
### 24-11-2020 - Release 6.1.1
    + Migration technique docker-platform (fabio, portworx, tomcat 9)
	
### 24-11-2020 - Release 6.1.2
    + Modification logstash_host géré par puppet au lieu de nomad.

### 28-07-2021 - Release 6.2.2
    + Add splay parameter to platines template to correct start synchronisation between BDD and Tomcat
	
### 30-01-2023 - Release 6.2.3
    + Changed haproxy image to use official image
	
### 01-08-2023 - Release 6.2.4
    + Separation of back, mock and front version

### 13-09-2023 - Release 6.2.5
    + Update Ciphers in haproxy configuration