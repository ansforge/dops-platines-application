# Base de données pour le développement

## Pour initialiser une base de données

### Prérequis

Il est nécessaire de disposer de bash et docker. Sous windows, le plus simple est de mulualiser pour cela
la [VM linux utilisée pour le devkit sessions](../session-devkit/README.md#deploy-windows).

### Création de la base sous docker

Pour créer le container de BDD, utiliser docker et lancer la commande suivante :

```bash
docker run --publish 127.0.0.1:3306:3306 --name "platines-test-base" \
  -e MARIADB_DATABASE=platines -e MARIADB_ROOT_PASSWORD=root \
  --volume <path/to>/<dump.sql.gz>:/docker-entrypoint-initdb.d/<dump.sql.gz> \
  mariadb:10.7
```

où il faut remplacer :

*   `<dump.sql.gz>` par le nom d'un dump
*   '</path/to>' par le chemin du dump sur votre disque local

## Réinitialiser le mot de passe d'un compte

Pour pouvoir vous connecter, vous avez besoin de disposer du mot de passe d'un compte.

Pour ce faire :

1.  Connectez vous au container de la base de données

    ```bash
       docker exec -ti platines-test-base /bin/bash
    ```

1.  Depuis le shell du container, connectez vous à mariadb

    ```bash
       mysql --password=root
    ```

1.  Une fois dans mysql, réinitialisez le mot de passe du compte qui vous intéresse gràce aux instructions 
    montrées ci-dessous :

    ```sql
       use platines;
       update utilisateur set password='$2a$10$4eLHw9HGkJMHLq3WepXNOO5oM1LdYFsOmOJipZ39unaAGyS3dHGZW' where mail = 'admin.platines@asipsante.fr';
       commit;
    ```

    Ces instructions peuvent être transposées à n'importe quel compte si on a l'email correspondant. Elles 
    réinitialisent le mote de passe à la valeur `Admin@2024`

# Tests

## Principe des tests d'intégration

Tout test d'intégration doit insérer en premier en mode `CLEAN_INSERT` le JDD de base.
Le ou les JDD spécifiques des différents tests sont à insérer en mode `REFRESH`.

Les données acl_class sont définies par exception sous forme de changesets liquibase.
Il ne faut **pas toucher** à la table `acl_class` dans les JDD dbunit.

Déclaration type d'un test :

```java
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestExecutionListeners({ 
    DependencyInjectionTestExecutionListener.class, 
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class, 
    DbUnitTestExecutionListener.class, 
    WithSecurityContextTestExecutionListener.class })
@ContextConfiguration(locations = { "/app-config.xml" })
@DatabaseSetup(value = ConstantesCommunesTests.REF_JDD_BASE, type = DatabaseOperation.CLEAN_INSERT)
@DatabaseSetup(value = "donnees-related_files.xml", type = DatabaseOperation.REFRESH)
@Order(11)
public class RelatedFilesControllerTest {
```

## Test [BulkProjectUpdateArchiveTest](src/test/java/fr/asipsante/platines/model/bulkupdate/BulkProjectUpdateArchiveTest.java) cassé lors des 'year bumps' de licence

C'est dû à la désynchronisation entre les sources xml de l'archive testée (qui servent de référence de contenu attendu) et l'archive.

Il suffit de reconstruire l'archive. Exemple de commandes shell pour le faire :

```sh
cd src/test/resources/fr/asipsante/platines/model/bulkupdate
zip test_upload_ROR.zip *.xml
```
