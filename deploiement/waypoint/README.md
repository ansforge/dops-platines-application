# Dépôt de déploiement waypoint pour platines

## Préquis

- Waypoint 0.11 minimum
- image ODR waypoint avec les AC de l'ANS
- beats-exporter déployé dans la plateforme
- pile ELK accessible

## Objectifs

Pour chacun des composants de l'application, il existe un descripteur nomad pour déployer le composant applicatif.

Ce descripteur permet de :

- déployer le composant en tant que tâche principale
- déployer une tâche filebeat ("sidecar") pour concenter les logs vers la pile ELK
- publier les métriques de filebeat pour superviser le bon envoi des logs (c'est le composant beats-exporter qui transforme les métriques publiées au format prometheus)

### Configuration des jobs

- Les configurations applicatives sont stockées dans vault (secret engine "platines")
- Les configurations pour le déploiement sont stockées dans le profil du runner waypoint : environnement (namespace), registry, datacenter.

### Jobs de type "service" déployés :

- database
- webapp
- reverse proxy

### Jobs de type "batch periodic" déployés

- platines-backup

## Paramètres à renseigner dans Vault (secrets engine : platines)

### Webapp

- tomcat/proxy_host
- tomcat/proxy_port
- tomcat/jobs_tmpl_repository (facultatif)
- tomcat/platines_mock_service_domain (domaine de base pour les mockservices)
- tomcat/default_truststore_external_url (URL de téléchargement du truststore par les mockservices si non renseigné dans l'application)
- tomcat/mail_alias
- tomcat/mail_password
- tomcat/mail_personal_name
- tomcat/mail_properties_mail_smtp_auth
- tomcat/mail_server_host
- tomcat/mail_server_port
- tomcat/mail_username
- tomcat/password
- tomcat/load_balancer_job_name (optionnel, valeur par défaut : pfc-fabio)

### Database

- mariadb/password
- mariadb/rootpassword
- mariadb/username

### Reverse-proxy

- haproxy/certificate
- haproxy/private_key
- front/platines_allowed_ips
- front/platines_public_hostname
