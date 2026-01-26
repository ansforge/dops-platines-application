/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.service.impl.nomad;

import fr.asipsante.platines.dto.SessionParameters;
import fr.asipsante.platines.exception.ServiceException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.nomad.client.ApiException;
import org.nomad.model.Job;
import org.nomad.model.Service;
import org.nomad.model.ServiceCheck;
import org.nomad.model.Task;
import org.nomad.model.TaskArtifact;
import org.nomad.model.TaskGroup;
import org.nomad.model.Template;
import org.nomad.model.Vault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(value = "job.manager", havingValue = "nomad", matchIfMissing = true)
public class NomadJobBuilder {

  public static final Logger LOGGER = LoggerFactory.getLogger(NomadJobBuilder.class);

  /** Message d'erreur pour la création d'un job. */
  private static final String ERROR_MESSAGE_CREATE_JOB = "Erreur lors de la création du job Nomad ";

  private static final String TRUSTSTORE_FILE_NAME = "truststore.jks";

  // Attention ! Nomad ne dézippe que les extensions standards des archives (zip,
  // tgz,...)
  private static final String MOCK_SERVICE_FILE_EXTENTION = ".war.zip";

  /**
   * Nom de la variable d'environnement utilisée pour transmettre le paramétrage des exceptions sur
   * les caractères restreints de la RFC HTTP 1.1
   */
  private static final String TOMCAT_RELAXED_QUERY_CHARS = "TOMCAT_RELAXED_QUERY_CHARS";

  @Value("${default.truststore.external.url}")
  private String DEFAULT_TRUSTSTORE_EXTERNAL_URL;

  @Value("${nomad.namespace:default}")
  private String NOMAD_PLATINES_NAMESPACE;

  @Value("${nomad.fabio.namespace:default}")
  private String NOMAD_FABIO_NAMESPACE;

  @Value("${mockservices.domain}")
  private String MOCK_SERVICE_DOMAIN;

  /** Url du repository des template de jobs. */
  @Value("${JOB_TMPL_REPOSITORY:}")
  private String REPOSITORY_JOBS;

  @Value("${mock.libs.url}")
  private String MOCK_LIBS_URL;

  /** Api du publisher. */
  private final String PUBLISHER_ENDPOINT = "insecure/publisher";

  /** Url de platines. */
  @Value("http://${tomcat.addr}${server.servlet.context-path}")
  private String URL_PLATINES;

  /** Utilisation de l'authentification mutuelle TLS. */
  private boolean mTls;

  @Autowired private NomadClient nomad;

  /** Mot de passe de keystore utilisé par les tâches nomad instanciées par ce service. */
  private final String taskSslKeystorePasswordString;

  public NomadJobBuilder(@Value("${generic.ssl.password}") String genericSslPassword) {
    this.taskSslKeystorePasswordString =
        Objects.requireNonNull(
            genericSslPassword,
            "The SSL_GENERIC_PASSWORD variable or an override through the generic.ssl.password"
                + " property is mandatory.");
  }

  /**
   * Créé un job client à partir d'un template json.
   *
   * @param jsonTmpl nomad job json template for client mock
   * @return le job client
   */
  public Job buildClientJob(SessionParameters sessionParameters) {
    Job job = null;
    try {
      job = nomad.getJobFromHCL(getHCLTemplate("client.nomad.hcl"));
      Set<String> datacenters = nomad.getDatacenters(NOMAD_PLATINES_NAMESPACE);
      for (String datacenter : datacenters) {
        job.addDatacentersItem(datacenter);
      }
      job.setNamespace(NOMAD_PLATINES_NAMESPACE);
      job.setName(sessionParameters.getUuidSession());
      job.setID(sessionParameters.getUuidSession());
      final Optional<TaskGroup> taskGroup = job.getTaskGroups().stream().findFirst();
      if (taskGroup.isPresent()) {
        taskGroup.get().setName("test-runner");
        final Optional<Task> task = taskGroup.get().getTasks().stream().findFirst();
        if (task.isPresent()) {

          final TaskArtifact taskArtifact = new TaskArtifact();
          final String urlSessionZip =
              URL_PLATINES + "insecure/download/" + sessionParameters.getUuidSession() + ".zip";
          taskArtifact.setGetterSource(urlSessionZip);
          taskArtifact.setRelativeDest("local/");
          final Map<String, String> getterOptions = new HashMap<>();
          getterOptions.put("archive", "false");
          taskArtifact.setGetterOptions(getterOptions);
          if (task.get().getArtifacts() == null) {
            task.get().setArtifacts(new ArrayList<TaskArtifact>());
          }
          task.get().addArtifactsItem(taskArtifact);
          task.get().setVault(buildVaultStanza());
          // Ajout des arguments à ceux définis dans le template
          List<String> initialArgs =
              (ArrayList<String>) task.get().getConfig().getOrDefault("args", new String[0]);
          // Ajout de la liste des fichiers de test a execute prefixe par l'option -t
          initialArgs.add("-t");
          for (int i = 0; i < sessionParameters.getArguments().size(); i++) {
            initialArgs.add("/local/" + sessionParameters.getArguments().get(i));
          }

          Map<String, String> env = new HashMap<>();
          env.put("SESSION_DIRECTORY", sessionParameters.getUuidSession());
          env.put("JAVA_APP_DIR", "/local");
          env.put("EXPORT_LOG", "true");
          env.put("KEYSTORE_PASSWORD", taskSslKeystorePasswordString);
          env.put("PLATINES_PUBLISHER", URL_PLATINES + PUBLISHER_ENDPOINT);
          task.get().env(env);
          task.get().addTemplatesItem(buildClientEnvTemplate());
        }
      }

    } catch (ApiException e1) {
      throw new ServiceException(e1, ERROR_MESSAGE_CREATE_JOB + sessionParameters.getUuidSession());
    }
    return job;
  }

  /**
   * Créé un job à partir d'un template json.
   *
   * @param jsonTmpl nomad job json template for server mock
   * @return nomad job
   */
  public Job buildMockJob(SessionParameters sessionParameters) {
    Job job = null;
    try {
      job = nomad.getJobFromHCL(getHCLTemplate("mock.nomad.hcl"));
      Set<String> datacenters = nomad.getDatacenters(NOMAD_PLATINES_NAMESPACE);
      for (String datacenter : datacenters) {
        job.addDatacentersItem(datacenter);
      }
      job.setNamespace(NOMAD_PLATINES_NAMESPACE);
      job.setName(sessionParameters.getUuidSession());
      job.setID(sessionParameters.getUuidSession());
      final Optional<TaskGroup> taskGroup = job.getTaskGroups().stream().findFirst();
      if (taskGroup.isPresent()) {
        final Optional<Task> task = taskGroup.get().getTasks().stream().findFirst();
        if (task.isPresent()) {
          Optional<Service> service = task.get().getServices().stream().findFirst();
          if (service.isPresent()) {
            Optional<ServiceCheck> check = service.get().getChecks().stream().findFirst();
            if (check.isPresent()) {
              check.get().setType("tcp");
              check.get().setPath("/");
              service.get().setName(sessionParameters.getUuidSession());
            }

            final List<String> tagsJobNomad = new ArrayList<>();
            // add tag to mockservice job to give Fabio visibility over this mockservice
            StringBuilder allowedIps = new StringBuilder();
            Iterator<String> ipIterator =
                Arrays.asList(sessionParameters.getAuthorizedIP().split(",")).iterator();
            while (ipIterator.hasNext()) {
              allowedIps.append("ip:").append(ipIterator.next());
              if (ipIterator.hasNext()) {
                allowedIps.append(",");
              }
            }
            // Fabio specific tag in order to add route to this mock server
            tagsJobNomad.add(
                "urlprefix-"
                    + sessionParameters.getUuidSession()
                    + "."
                    + MOCK_SERVICE_DOMAIN
                    + "/"
                    + " proto=tcp"
                    + " allow="
                    + allowedIps.toString());
            service.get().setTags(tagsJobNomad);
          }
          // Map des variables d'environnement
          Map<String, String> env = new HashMap<>();

          final TaskArtifact warArtifact = new TaskArtifact();
          final String urlWar =
              URL_PLATINES
                  + "insecure/download/"
                  + sessionParameters.getUuidSession()
                  + MOCK_SERVICE_FILE_EXTENTION;
          warArtifact.setGetterSource(urlWar);
          /* On extrait l'archive dans un répertoire mockservice afin de pouvoir faire la
           * bind plus facilement dans le template HCL.
           * Attention, cela modifie le context path du mockservice qui ne contiendra plus
           * l'id de session.
           * Cela simplifiera l'URL pour l'utilisateur
           */
          warArtifact.setRelativeDest("local/mockservice");
          // Ajout des librairies
          final TaskArtifact libsArtifact = new TaskArtifact();
          libsArtifact.setGetterSource(MOCK_LIBS_URL);
          libsArtifact.setRelativeDest("tmp/");

          if (task.get().getArtifacts() == null) {
            task.get().setArtifacts(new ArrayList<TaskArtifact>());
          }
          task.get().addArtifactsItem(warArtifact);
          task.get().addArtifactsItem(libsArtifact);
          // Création de l'artifact certificat serveur.
          if (!"".equals(sessionParameters.getKeystoreFile())) {
            final String urlKeystore =
                URL_PLATINES + "insecure/download/" + sessionParameters.getUuidSession() + ".p12";
            final TaskArtifact certificateArtifact = new TaskArtifact();
            certificateArtifact.setGetterSource(urlKeystore);
            certificateArtifact.setRelativeDest("secrets/");
            task.get().addArtifactsItem(certificateArtifact);
            env.put("KEYSTORE_FILE", sessionParameters.getUuidSession() + ".p12");
            env.put("KEYSTORE_FILE_DIR", "/secrets");
          }

          env.put("KEYSTORE_PASSWORD", taskSslKeystorePasswordString);
          // Il faut configurer le truststore même si mTLS est positionné à "none"
          final TaskArtifact truststoreArtifact = new TaskArtifact();
          String urlTruststore;
          if (!"".equals(sessionParameters.getTruststoreFile())) {
            // Get the truststore from the application configuration
            urlTruststore =
                URL_PLATINES
                    + "insecure/download/"
                    + sessionParameters.getUuidSession()
                    + "-truststore.jks";
            truststoreArtifact.setGetterSource(urlTruststore);
          } else {
            // Use default external truststore
            truststoreArtifact.setGetterSource(DEFAULT_TRUSTSTORE_EXTERNAL_URL);
          }
          truststoreArtifact.setGetterMode("file");
          truststoreArtifact.setRelativeDest("secrets/" + TRUSTSTORE_FILE_NAME);
          // Configure getter options to ignore SSL verification
          final Map<String, String> truststoreGetterOptions = new HashMap<>();
          truststoreGetterOptions.put("insecure", "true");
          truststoreArtifact.setGetterOptions(truststoreGetterOptions);
          task.get().addArtifactsItem(truststoreArtifact);
          env.put("TRUSTSTORE_FILE", TRUSTSTORE_FILE_NAME);
          // Activer l'authentification mutuelle
          // https://tomcat.apache.org/tomcat-11.0-doc/config/http.html#SSL_Support
          if (mTls) {
            env.put("MTLS", "required");
          } else {
            env.put("MTLS", "none");
          }

          // WS publication des resultats
          env.put("PLATINES_PUBLISHER", URL_PLATINES + PUBLISHER_ENDPOINT);
          env.put("SESSION", sessionParameters.getUuidSession());

          // Paramétrage des exceptions sur les caractères restreints de la RFC HTTP 1.1
          env.put(TOMCAT_RELAXED_QUERY_CHARS, sessionParameters.getRelaxedQueryChars());

          task.get().env(env);
          task.get().setVault(buildVaultStanza());
          task.get().addTemplatesItem(buildMockEnvTemplate());
          task.get().addTemplatesItem(buildMockSoapuiSettings());
        }
      }

    } catch (ApiException e1) {
      throw new ServiceException(e1, ERROR_MESSAGE_CREATE_JOB + sessionParameters.getUuidSession());
    }
    return job;
  }

  private Template buildMockEnvTemplate() {
    // Template pour accéder à Vault à partir du namespace
    String content =
        """
						{{with secret "%s/tomcat"}}
						JAVA_TOOL_OPTIONS="-Dorg.apache.tomcat.util.digester.PROPERTY_SOURCE=org.apache.tomcat.util.digester.EnvironmentPropertySource -Djavax.net.debug=ssl,handshake -Duser.timezone=Europe/Paris -Dhttp.proxyHost={{.Data.data.proxy_host}} -Dhttp.proxyPort={{.Data.data.proxy_port}} -Dhttps.proxyHost={{.Data.data.proxy_host}} -Dhttps.proxyPort={{.Data.data.proxy_port}} -Dfile.encoding=UTF-8"
						{{end}}
						""";
    Template envTemplate = new Template();
    envTemplate.setDestPath("secrets/file.env");
    envTemplate.setChangeMode("noop");
    envTemplate.setEnvvars(true);
    envTemplate.setEmbeddedTmpl(String.format(content, NOMAD_PLATINES_NAMESPACE));
    return envTemplate;
  }

  private Template buildClientEnvTemplate() {
    // Template pour accéder à Vault à partir du namespace
    String content =
        """
						{{with secret "%s/tomcat"}}
						JAVA_TOOL_OPTIONS="-Xmx1638m -Xms512m -Dfile.encoding=UTF8 -Duser.timezone=Europe/Paris -Dhttp.proxyHost={{.Data.data.proxy_host}} -Dhttp.proxyPort={{.Data.data.proxy_port}} -Dhttps.proxyHost={{.Data.data.proxy_host}} -Dhttps.proxyPort={{.Data.data.proxy_port}} -Dhttps.nonProxyHosts=10.0.0.0/8 -Dhttp.nonProxyHosts=10.0.0.0/8"
						{{end}}
				""";
    Template envTemplate = new Template();
    envTemplate.setDestPath("secrets/file.env");
    envTemplate.setChangeMode("noop");
    envTemplate.setEnvvars(true);
    envTemplate.setEmbeddedTmpl(String.format(content, NOMAD_PLATINES_NAMESPACE));
    return envTemplate;
  }

  /**
   * Build vault stanza
   *
   * @return Vault stanza
   */
  private Vault buildVaultStanza() {
    Vault vaultStanza = new Vault();
    vaultStanza.addPoliciesItem(NOMAD_PLATINES_NAMESPACE);
    vaultStanza.setChangeMode("restart");
    return vaultStanza;
  }

  private Template buildMockSoapuiSettings() {
    Template soapUiConfigTemplate = new Template();
    soapUiConfigTemplate.setChangeMode("noop");
    soapUiConfigTemplate.setDestPath("secrets/soapui-settings.xml");
    String content =
        """
<?xml version="1.0" encoding="UTF-8"?>
<con:soapui-settings xmlns:con="http://eviware.com/soapui/config">
{{with secret "%s/tomcat"}}
  <con:setting id="ProxySettings@autoProxy">true</con:setting>
  <con:setting id="ProxySettings@enableProxy">true</con:setting>
  <con:setting id="ProxySettings@host">{{.Data.data.proxy_host}}</con:setting>
  <con:setting id="ProxySettings@port">{{.Data.data.proxy_port}}</con:setting>
{{end}}
  <con:setting id="WsdlSettings@strict-schema-types">false</con:setting>
</con:soapui-settings>
				""";
    soapUiConfigTemplate.setEmbeddedTmpl(String.format(content, NOMAD_PLATINES_NAMESPACE));

    return soapUiConfigTemplate;
  }

  /**
   * Recupère le template json du job.
   *
   * @return le template
   */
  private String getHCLTemplate(String templateName) {
    StringBuilder content;
    if (!REPOSITORY_JOBS.isBlank()) {
      final HttpClient httpClient = HttpClientBuilder.create().build();
      String urlHCLTmpl;
      urlHCLTmpl = REPOSITORY_JOBS + "/" + templateName;
      final HttpGet get = new HttpGet(urlHCLTmpl);
      try {
        final HttpResponse response = httpClient.execute(get);
        final BufferedReader br =
            new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        content = new StringBuilder();
        String line;
        while (null != (line = br.readLine())) {
          content.append(line + "\n");
        }
      } catch (final IOException e) {
        throw new ServiceException(
            e, "Problème lors de la récupération du template HCL pour la session");
      }
    } else {
      // template embarqué
      try {
        BufferedReader br;
        br =
            new BufferedReader(
                new InputStreamReader(
                    Objects.requireNonNull(
                        getClass().getClassLoader().getResourceAsStream(templateName))));
        content = new StringBuilder();
        String line;
        while (null != (line = br.readLine())) {
          content.append(line + "\n");
        }
      } catch (final IOException e) {
        throw new ServiceException(
            e, "Problème lors de la récupération du template HCL pour la session");
      }
    }
    final String hclContent = content.toString();
    LOGGER.trace("HCL template content :\n{}", hclContent);
    return hclContent;
  }
}
