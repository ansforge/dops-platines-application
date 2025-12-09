/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.service.impl.listeners;

import fr.asipsante.platines.dto.TestSessionDto;
import fr.asipsante.platines.entity.enums.SessionStatus;
import fr.asipsante.platines.service.JobManager;
import fr.asipsante.platines.service.SessionService;
import fr.asipsante.platines.service.impl.common.SessionStateManager;
import java.time.Duration;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * @author apierre
 */
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class RunnerSessionListener extends Thread {

  /** LOGGER. */
  private static final Logger LOGGER = LoggerFactory.getLogger(RunnerSessionListener.class);

  private static final Long DURATION = 30L;

  private static final Long SLEEP = 1000L;

  @Autowired private SessionService iSessionService;

  private TestSessionDto sessionDto;

  /** SessionStateManager. */
  @Autowired private SessionStateManager sessionStateManager;

  /** JobManager. */
  private JobManager jobManager;

  private SecurityContext ctx;

  public RunnerSessionListener() {
    this.ctx = SecurityContextHolder.getContext();
  }

  @Override
  public void run() {
    try {

      SecurityContextHolder.setContext(ctx);

      DateTime now = new DateTime();
      final DateTime timer = now.plus(Duration.ofSeconds(DURATION).toMillis());

      do {
        now = new DateTime();
        try {
          sleep(SLEEP);
        } catch (final InterruptedException e) {
          LOGGER.debug("Thread interrompue : ", e);
        }
      } while (now.isBefore(timer));

      final TestSessionDto session = iSessionService.getSessionById(sessionDto.getId());
      if (SessionStatus.STARTING.equals(sessionDto.getSessionStatus())) {
        LOGGER.debug("Erreur lors de l'execution de la session");
        session.setSessionStatus(SessionStatus.ERROR);
        iSessionService.updateSession(sessionDto);
        jobManager.stopJob();
        sessionStateManager.publishSessionsState();
        sessionStateManager.publishSessionState(session.getUuid());
      }
    } finally {
      SecurityContextHolder.clearContext();
    }
  }

  /**
   * @param sessionDto the sessionDto to set
   */
  public void setSessionDto(TestSessionDto sessionDto) {
    this.sessionDto = sessionDto;
  }

  /**
   * @param jobManager the jobManager to set
   */
  public void setJobManager(JobManager jobManager) {
    this.jobManager = jobManager;
  }
}
