/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.service.impl.nomad;

import com.fasterxml.jackson.databind.DeserializationFeature;
import fr.ans.platines.nomad.reader.LogStreamResponseProvider;
import fr.asipsante.platines.exception.ServiceException;
import fr.asipsante.platines.exception.ServiceNomadException;
import fr.asipsante.platines.model.LogFrame;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import lombok.Getter;
import org.nomad.api.AllocationsApi;
import org.nomad.api.ClientApi;
import org.nomad.api.DeploymentsApi;
import org.nomad.api.EvaluationsApi;
import org.nomad.api.JobsApi;
import org.nomad.api.NodesApi;
import org.nomad.client.ApiClient;
import org.nomad.client.ApiException;
import org.nomad.client.Configuration;
import org.nomad.model.Allocation;
import org.nomad.model.AllocationListStub;
import org.nomad.model.Deployment;
import org.nomad.model.DeploymentState;
import org.nomad.model.Evaluation;
import org.nomad.model.Job;
import org.nomad.model.JobRegisterRequest;
import org.nomad.model.JobRegisterResponse;
import org.nomad.model.JobsParseRequest;
import org.nomad.model.LogStream;
import org.nomad.model.NetworkResource;
import org.nomad.model.NodeListStub;
import org.nomad.model.Port;
import org.nomad.model.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Class permettant la communication entre la plateforme et le serveur Nomad.
 *
 * @author cnader
 */
@Component
@ConditionalOnProperty(value = "job.manager", havingValue = "nomad", matchIfMissing = true)
@Getter
public class NomadClient {

  /** LOGGER. */
  private static final Logger LOG = LoggerFactory.getLogger(NomadClient.class);

  /** Délais entre chaque requête Http. */
  private static final long SLEEP_BETWEEN_REQUEST = 5000;

  /** Message d'erreur pour la création d'un job. */
  private static final String ERROR_MESSAGE_CREATE_JOB = "Erreur lors de la création du job Nomad ";

  /** Message d'erreur thread interrompue. */
  private static final String ERROR_THREAD_INTERRUPTED =
      "Thread interrrompue lors de la création du job nomad ";

  /** Constante pour convertir des minutes en millisecondes. */
  private static final long CONVERTING_MINUTES = 60000;

  /** Nomad api. */
  private final ApiClient client;

  /**
   * Constructeur.
   *
   * @param host the host nomad
   * @param port the port nomad
   */
  public NomadClient(String host, int port) {
    client = Configuration.getDefaultApiClient();
    client.getServers().clear();
    client.setBasePath("http://" + host + ":" + port + "/v1");
    LOG.info("Nomad agent address : {}:{}", host, port);
    client
        .getJSON()
        .getMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    client.getHttpClient().register(LogStreamResponseProvider.class);
    client.setDebugging(Boolean.parseBoolean(System.getProperty("nomad.api.debug")));
  }

  public String runJob(Job job, String namespace) throws ApiException {
    JobsApi jobApi = new JobsApi(client);
    JobRegisterResponse response =
        jobApi.postJob(job.getID(), new JobRegisterRequest().job(job), null, namespace, null, null);
    LOG.debug("Evaluation : {} en cours", response.getEvalID());
    return response.getEvalID();
  }

  public void deleteJob(String sessionUuid, String namespace) throws ApiException {
    JobsApi jobApi = new JobsApi(client);
    jobApi.deleteJob(sessionUuid, null, namespace, null, null, null, true);
  }

  public String getJobStatus(String sessionUuid, String namespace) throws ApiException {
    JobsApi jobApi = new JobsApi(client);
    final List<AllocationListStub> allocationListStubs =
        jobApi.getJobAllocations(
            sessionUuid, null, namespace, null, null, null, null, null, null, null, null);
    String status = allocationListStubs.stream().findFirst().get().getClientStatus();
    LOG.debug("Statut du Job : {}", status);
    return status;
  }

  public Set<String> getDatacenters(String namespace) throws ApiException {
    final NodesApi nodesApi = new NodesApi(client);
    final List<NodeListStub> listNodes =
        nodesApi.getNodes(null, namespace, null, null, null, null, null, null, null, null);
    final Set<String> datacenters = new HashSet<>();
    for (final NodeListStub nodeListStub : listNodes) {
      datacenters.add(nodeListStub.getDatacenter());
    }
    return datacenters;
  }

  /**
   * Récupère le statut de l'évaluation.
   *
   * @param evaluationId l'id de l'évaluation
   * @return l'évaluation
   * @throws ServiceException ServiceException
   */
  public Evaluation getEvaluationStatus(String evaluationId, String namespace, long timeout) {
    EvaluationsApi evalApi = new EvaluationsApi(client);
    boolean evalComplete = false;
    Evaluation evaluation;

    Date now = new Date();
    final Date finalTime = new Date(now.getTime() + (timeout * CONVERTING_MINUTES));
    do {
      try {
        evaluation =
            evalApi.getEvaluation(
                evaluationId, null, namespace, null, null, null, null, null, null, null);
        if ("complete".equals(evaluation.getStatus())
            || "canceled".equals(evaluation.getStatus())) {
          evalComplete = true;
        } else if ("failed".equals(evaluation.getStatus())) {
          throw new ServiceException(ERROR_MESSAGE_CREATE_JOB);
        }
        Thread.sleep(SLEEP_BETWEEN_REQUEST);
        now = new Date();
      } catch (ApiException e) {
        throw new ServiceNomadException(
            e, "Erreur nomad lors de l'interrogation de l'évaluation " + evaluationId);
      } catch (final InterruptedException e) {
        throw new ServiceException(e, ERROR_THREAD_INTERRUPTED);
      }
    } while (!evalComplete && now.before(finalTime));

    return evaluation;
  }

  /**
   * Statut de l'allocation.
   *
   * @param deploymentId, id du déploiement
   * @return true si il est déployé
   */
  public String getAllocationStatus(String uuidSession, String namespace) {
    JobsApi jobApi = new JobsApi(client);
    AllocationsApi allocApi = new AllocationsApi(client);
    Allocation allocation;
    String status;

    try {
      final Optional<AllocationListStub> allocationListStub =
          jobApi
              .getJobAllocations(
                  uuidSession, null, namespace, null, null, null, null, null, null, null, null)
              .stream()
              .findFirst();
      allocation =
          allocApi.getAllocation(
              allocationListStub.get().getID(),
              null,
              namespace,
              null,
              null,
              null,
              null,
              null,
              null,
              null);
      status = allocation.getClientStatus();
      LOG.debug("Statut de l'allocation : {}", status);
    } catch (ApiException e1) {
      throw new ServiceNomadException(e1, "Erreur nomad lors de l'interrogation de l'allocation ");
    }
    return status;
  }

  /**
   * Statut de l'allocation.
   *
   * @param uuidSession, id de la session
   * @return true si il est healthy
   */
  public boolean isAllocationHealthy(String uuidSession, String namespace) {
    JobsApi jobApi = new JobsApi(client);
    Deployment deployment;
    Map<String, DeploymentState> deploymentsStateMap;
    try {
      deployment =
          jobApi.getJobDeployment(
              uuidSession, null, namespace, null, null, null, null, null, null, null);
      deploymentsStateMap = deployment.getTaskGroups();
      DeploymentState deploymentState = deploymentsStateMap.get("server-mock");
      LOG.debug("Nombre d'allocations en bonne santé : {}", deploymentState.getHealthyAllocs());
      return deploymentState.getHealthyAllocs() > deploymentState.getUnhealthyAllocs();
    } catch (ApiException e1) {
      throw new ServiceNomadException(e1, "Erreur nomad lors de l'interrogation de l'allocation ");
    }
  }

  /**
   * Statut du déploiement.
   *
   * @param deploymentId, id du déploiement
   * @return true si il est déployé
   */
  public String getDeploymentStatus(String deploymentId, String namespace) {
    DeploymentsApi deploymentApi = new DeploymentsApi(client);
    String status;
    Deployment deployment;

    try {
      deployment =
          deploymentApi.getDeployment(
              deploymentId, null, namespace, null, null, null, null, null, null, null);
      status = deployment.getStatus();
      LOG.debug("Statut du déploiement : {}", status);
    } catch (ApiException e1) {
      throw new ServiceNomadException(
          e1, "Erreur nomad lors de l'interrogation du déploiement " + deploymentId);
    }

    return status;
  }

  public LogFrame getLogsContent(
      String uuid, String namespace, String typeLogs, int offset, String origin) {
    return getLogsContent(uuid, namespace, typeLogs, offset, "", origin);
  }

  /**
   * retourne le contenu des logs tomcat.
   *
   * @param allocID the uuid de l'allocation
   * @param typeLogs the type logs
   * @param offsetMap the offset map
   * @return les logs
   */
  public LogFrame getLogsContent(
      String allocID, String namespace, String typeLogs, int offset, String filter, String origin) {
    ClientApi nomadClient = new ClientApi(client);
    AllocationsApi allocationApi = new AllocationsApi(client);
    final StringBuilder logs = new StringBuilder();
    LogFrame frame = new LogFrame();
    try {

      Allocation alloc =
          allocationApi.getAllocation(
              allocID, null, namespace, null, null, null, null, null, null, null);
      List<Task> tasks = alloc.getJob().getTaskGroups().stream().findFirst().get().getTasks();
      String taskName = tasks.stream().findFirst().get().getName();
      LogStream logStream =
          nomadClient.streamLogs(taskName, typeLogs, allocID, false, offset, origin, false, null);
      if (logStream != null) {
        LOG.debug("LogStream offset : {}", logStream.getOffset());
        if (logStream.getData() != null) {
          byte[] decodedBytes = Base64.getDecoder().decode(logStream.getData());
          String content = new String(decodedBytes);
          LOG.debug(
              "Longueur du log : {} (Allocation {}, tâche {}, type de logs {}.",
              content.length(),
              allocID,
              taskName,
              typeLogs);
          if (!filter.isEmpty()) {
            String[] lines = content.split("\\r?\\n");
            for (String line : lines) {
              if (line.contains(filter)) {
                logs.append(line).append(System.getProperty("line.separator"));
              }
            }
          } else {
            logs.append(content);
          }
          frame.setFrame(
              new ByteArrayInputStream(logs.toString().getBytes(StandardCharsets.ISO_8859_1)));
          frame.setOffset(logStream.getOffset());
        } else {
          frame.setOffset(offset);
          frame.setFrame(InputStream.nullInputStream());
          LOG.debug(
              "Aucun log collecté sur l'allocation {} - {} ({})", allocID, taskName, typeLogs);
        }
      } else {
        frame.setOffset(offset);
        frame.setFrame(InputStream.nullInputStream());
        LOG.debug("Aucun log collecté sur l'allocation {} - {} ({})", allocID, taskName, typeLogs);
      }

    } catch (ApiException e) {
      LOG.info("Nomad failed to respond : {}", client.getBasePath(), e);
    }
    return frame;
  }

  /**
   * retourne les id des allocations d'un job nomad
   *
   * @param jobName the job name
   * @return la liste des ID.
   */
  public List<String> getAllocIDsByJobName(String jobName, String namespace) {
    List<String> allocIDs = new ArrayList<>();
    JobsApi jobsApi = new JobsApi(client);
    try {
      List<AllocationListStub> allocations =
          jobsApi.getJobAllocations(
              jobName, null, namespace, null, null, null, null, null, null, null, null);
      for (AllocationListStub alloc : allocations) {
        allocIDs.add(alloc.getID());
      }
    } catch (ApiException e) {
      LOG.info("Nomad failed to respond : {}, jobname : {}", client.getBasePath(), jobName, e);
    }
    return allocIDs;
  }

  public Job getJobFromHCL(String hclTemplate) throws ApiException {
    JobsApi jobApi = new JobsApi(client);
    JobsParseRequest jobParseRequest = new JobsParseRequest();
    jobParseRequest.jobHCL(hclTemplate);
    jobParseRequest.canonicalize(true);
    LOG.info("Endpoint base url : {}", jobApi.getApiClient().getBasePath());
    return jobApi.postJobParse(jobParseRequest);
  }

  public String getAddressByJobName(String uuidSession, String namespace)
      throws IOException, ApiException {
    AllocationsApi allocApi = new AllocationsApi(client);
    List<String> allocIDs = getAllocIDsByJobName(uuidSession, namespace);
    // Only one alloc/job is allowed in this signature
    String allocID = allocIDs.stream().findFirst().get();
    Allocation mockServerAllocation =
        allocApi.getAllocation(allocID, null, namespace, null, null, null, null, null, null, null);
    NetworkResource network = mockServerAllocation.getResources().getNetworks().get(0);
    List<Port> ports = network.getDynamicPorts();
    if (ports != null) {
      Optional<Port> port = ports.stream().filter(p -> "httpsmock".equals(p.getLabel())).findAny();
      if (port.isPresent()) {
        LOG.debug("Address to filter = {}:{}", network.getIP(), port.get().getValue());
        return network.getIP() + ":" + port.get().getValue();
      }
    }
    return "";
  }
}
