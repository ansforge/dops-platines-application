/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.service.impl.listeners;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import fr.asipsante.platines.dto.TestSessionDto;
import fr.asipsante.platines.entity.enums.SessionStatus;
import fr.asipsante.platines.mail.entity.Mail;
import fr.asipsante.platines.model.LogFrame;
import fr.asipsante.platines.service.JobManager;
import fr.asipsante.platines.service.MailService;
import fr.asipsante.platines.service.StreamType;
import fr.asipsante.platines.service.impl.common.SessionStateManager;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.util.HtmlUtils;

/**
 * @author apierre
 */
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class MockSessionListener extends Thread {

  /** LOGGER. */
  private static final Logger LOGGER = LoggerFactory.getLogger(MockSessionListener.class);

  /** Sujet du mail lors de la fin d'une session de type mockservice. */
  private static final String SUBJECT_MAIL_END = "Platines: Email de fermeture du mockservice";

  /** SLEEP. */
  private static final long SLEEP = 5000;

  /** IMailService. */
  @Autowired private MailService mailService;

  /** SessionStateManager. */
  @Autowired private SessionStateManager sessionStateManager;

  /** JobManager. */
  private JobManager jobManager;

  /** Url. */
  private String url;

  /** TestSessionDto. */
  private TestSessionDto session;

  /** SimpMessagingTemplate. */
  private final SimpMessagingTemplate template;

  /**
   * Security context from the mock launch request. It gets injected in the listener thread's
   * context.
   */
  private final SecurityContext ctx;

  /**
   * Constructeur par défaut.
   *
   * @param template SimpMessagingTemplate
   */
  @Autowired
  public MockSessionListener(SimpMessagingTemplate template) {
    this.template = template;
    this.ctx =
        Objects.requireNonNull(SecurityContextHolder.getContext(), "Missing security context");
  }

  /** exécute la session. */
  @Override
  public void run() {
    try {
      SecurityContextHolder.setContext(ctx);
      DateTime dateTime = new DateTime();
      final Long sessionDurationHours = this.session.getSessionDuration().getDuration();
      final DateTime dateTime2 = dateTime.plus(Duration.ofMinutes(sessionDurationHours).toMillis());
      LOGGER.info(
          "Starting session {} at {}. Planned end of session, {}",
          session.getUuid(),
          dateTime,
          dateTime2);
      List<String> logsStdOut;
      List<String> logsStdErr;
      List<String> logsConn;

      do {
        try {
          sleep(SLEEP);
          final LogFrame stdout = jobManager.streamStdoutLog();
          if (stdout != null) {
            logsStdOut = filter(stdout.getFrame());
            this.template.convertAndSend(
                "/topic/mock." + this.session.getUuid() + ".stdout", logsStdOut);
            LOGGER.debug("Valeur de l'offset stdout : {}", stdout.getOffset());
          }

          final LogFrame stderr = jobManager.streamStderrLog();
          if (stderr != null) {
            logsStdErr = filter(stderr.getFrame());
            this.template.convertAndSend(
                "/topic/mock." + this.session.getUuid() + ".stderr", logsStdErr);
            LOGGER.debug("Valeur de l'offset stderr : {}", stderr.getOffset());
          }

          final Map<String, LogFrame> accesslogs =
              jobManager.streamConnectionLogs(StreamType.STDERR, false);
          if (!accesslogs.isEmpty()) {
            Set<String> keys = accesslogs.keySet();
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            for (String key : keys) {
              out.write(accesslogs.get(key).getFrame().readAllBytes());
            }

            logsConn =
                new BufferedReader(
                        new InputStreamReader(new ByteArrayInputStream(out.toByteArray())))
                    .lines()
                    .collect(Collectors.toList());
            this.template.convertAndSend(
                "/topic/connection." + this.session.getUuid() + ".stderr", logsConn);
          }
          dateTime = new DateTime();
        } catch (final InterruptedException | IOException e) {
          LOGGER.error("Problème lors de l'envoi du mail de fin de session serveur :", e);
        }
      } while (dateTime.isBefore(dateTime2));

      // En fin de session, on collecte les logs stderr et stdout dans un zip
      byte[] logsZipped = jobManager.getSessionLogs();
      sessionStateManager.saveSessionLogs(this.session.getUuid(), logsZipped);
      LOGGER.info("Ending session {} (begun at {})", session.getUuid(), dateTime);
      stopJob();

      sessionStateManager.updateSessionTestStatus(session.getUuid(), SessionStatus.FINISHED);
      sessionStateManager.updateAndPublishSessionStatus(session.getId(), SessionStatus.FINISHED);
      sessionStateManager.publishSessionState(session.getUuid());
    } finally {
      SecurityContextHolder.clearContext();
    }
  }

  public void setSession(TestSessionDto session) {
    this.session = session;
  }

  public void setJobManager(JobManager jobManager) {
    this.jobManager = jobManager;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  private void stopJob() {
    jobManager.stopJob();
    final Mail mail = new Mail();
    mail.setMailTo(this.session.getApplication().getUser().getMail());
    mail.setMailSubject(SUBJECT_MAIL_END);
    mail.setTemplate("/velocity/mail/end_session_server.vm");
    final Map<String, Object> model = new HashMap<>();
    mail.setVariables(model);
    model.put("uuid", this.url);
    model.put(
        "firstName", HtmlUtils.htmlEscape(this.session.getApplication().getUser().getForename()));
    mailService.sendEmail(mail);
  }

  private List<String> filter(InputStream is) {
    List<String> rawContent =
        new BufferedReader(new InputStreamReader(is)).lines().collect(Collectors.toList());
    // On ne publie pas les logs de niveau INFO
    return rawContent.stream().filter(line -> !line.contains("INFO")).collect(Collectors.toList());
  }
}
