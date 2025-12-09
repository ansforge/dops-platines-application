/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.service.impl.common;

import fr.asipsante.platines.dao.ITestSessionDao;
import fr.asipsante.platines.dto.TestSessionListDto;
import fr.asipsante.platines.entity.TestSession;
import fr.asipsante.platines.entity.enums.SessionStatus;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author aboittiaux
 */
@Service
public class SessionStateManager {

  /** ITestSessionDao. */
  @Autowired
  @Qualifier("testSessionDao")
  private ITestSessionDao sessionDao;

  /** DateConverter. */
  @Autowired private DateConverter dateConverter;

  /** SimpMessagingTemplate. */
  private final SimpMessagingTemplate template;

  /**
   * Constructeur.
   *
   * @param template SimpMessagingTemplate
   */
  @Autowired
  public SessionStateManager(SimpMessagingTemplate template) {
    this.template = template;
  }

  /**
   * Modifie le statut de la session.
   *
   * @param idSession, id de la session à modifier
   * @param status, nouveau statut à enregistrer
   */
  public void updateAndPublishSessionStatus(Long idSession, SessionStatus status) {

    final TestSessionListDto session = new TestSessionListDto();
    session.setId(idSession);
    session.setSessionStatus(status);
    this.template.convertAndSend("/topic/sessions", session);
    this.template.convertAndSend("/topic/sessions." + idSession, status);
  }

  /** publie l'état des sessions. */
  public void publishSessionsState() {
    this.template.convertAndSend("/topic/sessions", "sessions");
  }

  /**
   * Publie l'état d'une session identifiée par son uuid.
   *
   * @param uuid, l'uuid de la session
   */
  public void publishSessionState(String uuid) {
    this.template.convertAndSend("/topic/sessions." + uuid, "sessions");
  }

  /**
   * Modifie le statut d'un resultProject.
   *
   * @param idRproject, id du projet
   * @param statusRProject, nouveau statut du projet
   */
  public void publishStateOfProjectResults(Long idRproject, String statusRProject) {
    final Map<Long, String> map = new HashMap<>();
    map.put(idRproject, statusRProject);
    this.template.convertAndSend("/topic/rproject." + idRproject, map);
  }

  /**
   * Modifie le statut d'une session identifiée par son uuid.
   *
   * @param uuidSession, uuid de la session
   * @param status, le nouveau statut de la session
   */
  @Transactional
  public void updateSessionTestStatus(String uuidSession, SessionStatus status) {
    final TestSession session = sessionDao.getSessionByUuid(uuidSession);
    session.setSessionStatus(status);
    sessionDao.update(session);
  }

  /**
   * Modifie le statut et la date d'exécution d'une session.
   *
   * @param uuidSession, uuid de la session
   * @param status, nouveau statut de la session
   */
  @Transactional
  public void updateSessionTestExecutionDate(String uuidSession, SessionStatus status) {
    final TestSession session = sessionDao.getSessionByUuid(uuidSession);
    session.setExecutionDate(dateConverter.convertToUTC(new Date()));
    session.setSessionStatus(status);
    sessionDao.update(session);
  }

  /**
   * Enrregistre les logs de la session
   *
   * @param uuidSession
   * @param logsSession
   */
  @Transactional(propagation = Propagation.REQUIRED)
  public void saveSessionLogs(String uuidSession, byte[] logsSession) {

    final TestSession session = sessionDao.getSessionByUuid(uuidSession);
    session.setLog(logsSession);
    sessionDao.updateSessionWithoutToken(session);
  }
}
