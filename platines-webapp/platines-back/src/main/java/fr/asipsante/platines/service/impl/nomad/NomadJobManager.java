/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.service.impl.nomad;

import fr.asipsante.platines.dto.SessionParameters;
import fr.asipsante.platines.entity.JobStatus;
import fr.asipsante.platines.entity.enums.Role;
import fr.asipsante.platines.entity.enums.SessionStatus;
import fr.asipsante.platines.exception.ServiceException;
import fr.asipsante.platines.exception.ServiceNomadException;
import fr.asipsante.platines.model.LogFrame;
import fr.asipsante.platines.service.JobManager;
import fr.asipsante.platines.service.PreferencesService;
import fr.asipsante.platines.service.StreamType;
import fr.asipsante.platines.service.impl.common.SessionStateManager;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.commons.io.IOUtils;
import org.nomad.client.ApiException;
import org.nomad.model.Evaluation;
import org.nomad.model.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Nomad job manager.
 *
 * @author apierre
 */
@Component
@ConditionalOnProperty(value = "job.manager", havingValue = "nomad", matchIfMissing = true)
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class NomadJobManager implements JobManager {

  public static final Logger LOGGER = LoggerFactory.getLogger(NomadJobManager.class);

  private static final int DEPLOYMENT_PROBE_PERIOD_MILLISECONDS = 500;

  /** Délais d'attente. */
  private static final long TIMEOUT = 60;

  /** Délais entre chaque requête Http. */
  private static final long SLEEP_BETWEEN_REQUEST = 5000;

  /** Délais d'attente lors du déploiement. */
  private static final long TIMEOUT_DEPLOYMENT = 600000;

  /** Constante pour convertir des minutes en millisecondes. */
  private static final long CONVERTING_MINUTES = 60000;

  /** Message d'erreur thread interrompue. */
  private static final String ERROR_THREAD_INTERRUPTED =
      "Thread interrrompue lors de la création du job nomad ";

  @Value("${nomad.namespace:default}")
  private String NOMAD_PLATINES_NAMESPACE;

  @Value("${nomad.fabio.namespace:default}")
  private String NOMAD_FABIO_NAMESPACE;

  @Value("${load.balancer.job.name:pfc-fabio}")
  private String LOAD_BALANCER_JOB_NAME;

  /** Map contenant les offsets des logs fabio a collecter */
  private Map<String, Integer> offsetMap = new HashMap<>();

  /** Offset des logs stdout */
  private int stdoutOffset;

  /** Offset des logs stderr */
  private int stderrOffset;

  /** identifiant de la session */
  private String uuidSession;

  /** Statut du job. */
  private JobStatus jobStatus = JobStatus.UNKNOW;

  /** Service des preferences. */
  @Autowired private PreferencesService preferencesService;

  /** Service des etats du manager. */
  @Autowired private SessionStateManager sessionStateManager;

  @Autowired private NomadClient nomad;

  @Autowired private NomadJobBuilder jobBuilder;

  /** {@inheritDoc} */
  @Override
  public void submitJob(SessionParameters sessionParameters) {
    this.uuidSession = sessionParameters.getUuidSession();
    Job job;
    String evalID;
    try {
      if ((Role.CLIENT).equals(sessionParameters.getApplicationRole())) {

        job = jobBuilder.buildMockJob(sessionParameters);
      } else {
        job = jobBuilder.buildClientJob(sessionParameters);
      }
      // On enregistre le Job dans Nomad (cela créé le job et le lance)
      LOGGER.trace("Job data after canonicalization : '{}'", job);
      evalID = nomad.runJob(job, NOMAD_PLATINES_NAMESPACE);
    } catch (ApiException e) {
      throw new ServiceException(
          e, "Erreur lors de la création du job " + sessionParameters.getUuidSession());
    }

    // Si le job est déployable (suffisament de ressources), on attend le résultat
    // de l'évaluation.
    long timeout;
    final String provisioningTimeout =
        preferencesService.getPreferences().getProperty("provisionning_timeout");
    if (provisioningTimeout == null) {
      timeout = TIMEOUT;
    } else {
      timeout = Long.parseLong(provisioningTimeout);
    }

    Evaluation evaluation = nomad.getEvaluationStatus(evalID, NOMAD_PLATINES_NAMESPACE, timeout);
    if (evaluation.getBlockedEval() != null && !"".equals(evaluation.getBlockedEval())) {
      sessionStateManager.updateSessionTestStatus(
          sessionParameters.getUuidSession(), SessionStatus.WAITING);
      sessionStateManager.publishSessionsState();
    }
    LOGGER.debug("Statut de l'évaluation : {}", evaluation.getStatus());
    if ("complete".equals(evaluation.getStatus())) {
      // Cas des mockservices
      if (Role.CLIENT.equals(sessionParameters.getApplicationRole())) {
        // On attend que le déploiement soit terminé (successful ou failed);
        jobStatus = waitUntilDeploymentIsSuccess(evaluation.getDeploymentID());
        // Si le déploiement est en succès, il faut maintenant attendre que l'allocation soit
        // "Healthy"
        if (JobStatus.RUNNING.equals(jobStatus) && !isHealthy()) {
          jobStatus = JobStatus.DEAD;
        }
        // Cas des testRunner (batches)
      } else if (Role.SERVER.equals(sessionParameters.getApplicationRole())) {
        // Pour les batchs, on attend le retour de l'allocation (à vérifier)
        jobStatus = waitUntilAllocationIsRunning();
      }
    }
  }

  /** {@inheritDoc} */
  @Override
  public JobStatus getJobStatus() {
    return jobStatus;
  }

  /** {@inheritDoc} */
  @Override
  public void stopJob() {
    stopJob(uuidSession);
  }

  /** {@inheritDoc} */
  @Override
  public void stopJob(String uuid) {
    try {
      nomad.deleteJob(uuid, NOMAD_PLATINES_NAMESPACE);
    } catch (ApiException e) {
      throw new ServiceNomadException(e, "Erreur lors de l'arrêt du job nomad " + uuidSession);
    }
  }

  @Override
  public LogFrame streamStderrLog() {
    return streamLogs(uuidSession, StreamType.STDERR, stderrOffset, "start");
  }

  @Override
  public LogFrame streamStdoutLog() {
    return streamLogs(uuidSession, StreamType.STDOUT, stdoutOffset, "start");
  }

  @Override
  public Map<String, LogFrame> streamConnectionLogs(StreamType type, boolean reset) {
    if (reset) {
      offsetMap.clear();
    }
    String filter;
    Map<String, LogFrame> frames = new HashMap<>();
    List<String> allocIDs =
        nomad.getAllocIDsByJobName(LOAD_BALANCER_JOB_NAME, NOMAD_FABIO_NAMESPACE);
    LOGGER.debug("Nombre d'allocations : {}", allocIDs.size());
    try { //
      filter = " to tcp://" + nomad.getAddressByJobName(uuidSession, NOMAD_PLATINES_NAMESPACE);
      for (String id : allocIDs) {
        if (offsetMap.get(id) == null) {
          // Initialisation de l'offset à 0
          offsetMap.put(id, 0);
        }
        int previousOffset = offsetMap.get(id);
        // Appel des logs avec Offset 1, juste pour récupérer l'offset courant.
        LOGGER.debug(
            "Contrôle du changement des logs de l'allocation {} avec l'offset : {}", id, 1);
        LogFrame frame =
            nomad.getLogsContent(id, NOMAD_FABIO_NAMESPACE, type.label, 1, filter, "end");

        int currentOffset = frame.getOffset();
        offsetMap.put(id, currentOffset);

        // Cas où le log a changé depuis le dernier appel
        LOGGER.debug("currentOffset : {}, previousOffset : {}", currentOffset, previousOffset);
        if (currentOffset > previousOffset) {
          // On récupère juste le delta depuis la fin
          LOGGER.debug(
              "Collecte des logs de l'allocation {} avec l'offset : {}",
              id,
              currentOffset - previousOffset);
          frame =
              nomad.getLogsContent(
                  id,
                  NOMAD_FABIO_NAMESPACE,
                  type.label,
                  currentOffset - previousOffset,
                  filter,
                  "end");
          // Il faut tronquer les lignes de Fabio pour ne pas afficher l'IP interne du container
          LogFrame truncatedLinesFrame = new LogFrame();
          // On tronque la fin de la ligne de contrôle d'accès :
          // 2023/08/11 09:39:24 [INFO] route rules denied access from 172.30.0.42 to
          // tcp://10.3.8.34:31947
          // 2023/08/11 09:39:24 [INFO] route rules denied access from 172.30.0.42
          truncatedLinesFrame.setFrame(truncateLine(frame.getFrame(), filter));
          truncatedLinesFrame.setOffset(frame.getOffset());
          frames.put(id, truncatedLinesFrame);

        } else {
          frames.put(id, new LogFrame(0, InputStream.nullInputStream()));
        }
      }

    } catch (ApiException | IOException e) {
      throw new ServiceNomadException(e, "Erreur lors de la collecte des logs : " + uuidSession);
    }
    return frames;
  }

  @Override
  public byte[] getSessionLogs() {
    try {
      // bug si les logs sont plus longs que l'offset max (131072), la fin sera tronquée
      final InputStream isOut = streamLogs(uuidSession, StreamType.STDOUT, 0, "start").getFrame();
      final InputStream isErr = streamLogs(uuidSession, StreamType.STDERR, 0, "start").getFrame();
      final byte[] arrayOut = IOUtils.toByteArray(isOut);
      final byte[] arrayErr = IOUtils.toByteArray(isErr);

      final ByteArrayOutputStream baos = new ByteArrayOutputStream();
      final ZipOutputStream zos = new ZipOutputStream(baos);

      final ZipEntry stdoutEntry = new ZipEntry("stdout.txt");
      stdoutEntry.setSize(arrayOut.length);
      zos.putNextEntry(stdoutEntry);
      zos.write(arrayOut);
      zos.closeEntry();

      final ZipEntry stderrEntry = new ZipEntry("stderr.txt");
      stderrEntry.setSize(arrayErr.length);
      zos.putNextEntry(stderrEntry);
      zos.write(arrayErr);
      zos.closeEntry();
      zos.close();
      baos.close();
      return baos.toByteArray();
    } catch (IOException e) {
      LOGGER.error("IOException :", e);
      return new byte[0];
    }
  }

  private LogFrame streamLogs(
      String uuidSession, StreamType streamType, int offset, String origin) {
    List<String> allocIDs = nomad.getAllocIDsByJobName(uuidSession, NOMAD_PLATINES_NAMESPACE);
    // Only one alloc/job is allowed in this signature
    String allocID = allocIDs.stream().findFirst().get();
    LOGGER.debug(
        "Paramètres de la requête : allocID {}, Type {}, offset-> {}",
        allocID,
        streamType.label,
        offset);
    LogFrame logFrame =
        nomad.getLogsContent(allocID, NOMAD_PLATINES_NAMESPACE, streamType.label, offset, origin);
    LOGGER.debug("Valeur de l'offset du flux {} : {}", streamType, logFrame.getOffset());
    switch (streamType) {
      case STDOUT:
        {
          LOGGER.debug("Type de stream : {}", streamType);
          stdoutOffset = logFrame.getOffset();
          LOGGER.debug("Valeur de stdOutOffset : {}", stdoutOffset);
          break;
        }
      case STDERR:
        {
          LOGGER.debug("Type de stream : {}", streamType);
          stderrOffset = logFrame.getOffset();
          LOGGER.debug("Valeur de stdErrOffset : {}", stdoutOffset);
          break;
        }
      default:
        LOGGER.debug("Type de flux inconnu : {}", streamType);
        // do nothing (impossible)
    }
    return logFrame;
  }

  /**
   * Statut de l'allocation.
   *
   * @return true si l'allocation est en cours d'exécution
   */
  private JobStatus waitUntilAllocationIsRunning() {

    boolean success = false;
    final Date now = new Date();
    final Date timeout = new Date(now.getTime() + (TIMEOUT_DEPLOYMENT * CONVERTING_MINUTES));
    String status;
    do {

      try {
        status = nomad.getAllocationStatus(uuidSession, NOMAD_PLATINES_NAMESPACE);
        LOGGER.debug("Statut de l'allocation en cours de déploiement : {}", status);
        if ("running".equals(status)) {
          success = true;
        }
        Thread.sleep(SLEEP_BETWEEN_REQUEST);
      } catch (final InterruptedException e) {
        throw new ServiceException(e, ERROR_THREAD_INTERRUPTED + uuidSession);
      }

    } while (!success && now.before(timeout));
    return convertJobStatus(status);
  }

  /**
   * Statut du déploiement.
   *
   * @param deploymentId, id du déploiement
   * @return le statut "running" si le job est déployé avec succès
   */
  private JobStatus waitUntilDeploymentIsSuccess(String deploymentId) {
    boolean success = false;
    final Date now = new Date();
    final Date timeout = new Date(now.getTime() + (TIMEOUT_DEPLOYMENT * CONVERTING_MINUTES));
    String status;
    do {
      status = nomad.getDeploymentStatus(deploymentId, NOMAD_PLATINES_NAMESPACE);
      if ("successful".equals(status)) {
        success = true;
      } else {
        try {
          if ("failed".equals(status)) {
            return convertJobStatus(status);
          }
          Thread.sleep(DEPLOYMENT_PROBE_PERIOD_MILLISECONDS);
        } catch (InterruptedException ex) {
          throw new RuntimeException(ex);
        }
      }
    } while (!success && now.before(timeout));
    return convertJobStatus(status);
  }

  /**
   * HealthCheck.
   *
   * @return true si le service est correctement supervisé par consul.
   */
  private boolean isHealthy() {
    boolean healthy = false;
    final Date now = new Date();
    final Date timeout = new Date(now.getTime() + (TIMEOUT_DEPLOYMENT * CONVERTING_MINUTES));
    do {
      healthy = nomad.isAllocationHealthy(uuidSession, NOMAD_PLATINES_NAMESPACE);
      try {
        Thread.sleep(SLEEP_BETWEEN_REQUEST);
      } catch (final InterruptedException e) {
        throw new ServiceException(e, ERROR_THREAD_INTERRUPTED + uuidSession);
      }
    } while (!healthy && now.before(timeout));

    return healthy;
  }

  /**
   * Converti le statut du job à partir d'une String
   *
   * @param status, le nouveau statut à enregistrer
   */
  private JobStatus convertJobStatus(String status) {
    if ("pending".equals(status)) {
      return JobStatus.PENDING;
    } else if ("canceled".equals(status)) {
      return JobStatus.CANCELLED;
    } else if ("complete".equals(status)) {
      return JobStatus.COMPLETE;
    } else if ("successful".equals(status)) {
      return JobStatus.RUNNING;
    } else if ("running".equals(status)) {
      return JobStatus.RUNNING;
    } else if ("blocked".equals(status)) {
      return JobStatus.QUEUED;
    } else {
      return JobStatus.DEAD;
    }
  }

  private InputStream truncateLine(InputStream is, String patternToTruncate) {
    List<String> content =
        new BufferedReader(new InputStreamReader(is)).lines().collect(Collectors.toList());
    List<String> truncatedContent = new ArrayList<>();
    for (String line : content) {
      int indexOfPattern = line.lastIndexOf(patternToTruncate);
      if (indexOfPattern > -1) {
        truncatedContent.add(line.substring(0, indexOfPattern));
      }
    }
    byte[] bytes = truncatedContent.stream().collect(Collectors.joining("\n", "", "\n")).getBytes();
    return new ByteArrayInputStream(bytes);
  }
}
