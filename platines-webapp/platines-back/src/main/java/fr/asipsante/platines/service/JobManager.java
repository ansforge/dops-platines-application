/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.service;

import fr.asipsante.platines.dto.SessionParameters;
import fr.asipsante.platines.entity.JobStatus;
import fr.asipsante.platines.model.LogFrame;
import java.util.Map;

/**
 * Job Manager for Nomad.
 *
 * @author apierre
 */
public interface JobManager {

  /**
   * Lance le job.
   *
   * @param les paramètres du job
   * @return le statut du job
   */
  void submitJob(SessionParameters sessionParameters);

  /**
   * Consulte le statut du job.
   *
   * @return le statut
   */
  JobStatus getJobStatus();

  /** Arrête le job courant. */
  void stopJob();

  /** Arrête un job à partir de son uuid. */
  void stopJob(String uuid);

  /**
   * Stream les logs de la sortie d'erreur
   *
   * @return the log stream
   */
  LogFrame streamStderrLog();

  /**
   * Stream les logs de la sortie standard
   *
   * @return the log stream
   */
  LogFrame streamStdoutLog();

  /**
   * Stream les access logs
   *
   * @param le type de log
   * @param reset les offset pour collecter le log en entier
   * @return la map des logs stream et l'id pour les retrouver
   */
  Map<String, LogFrame> streamConnectionLogs(StreamType type, boolean reset);

  /**
   * Collecte tous les logs de la session (stdout et stderr)
   *
   * @return les logs.
   */
  byte[] getSessionLogs();
}
