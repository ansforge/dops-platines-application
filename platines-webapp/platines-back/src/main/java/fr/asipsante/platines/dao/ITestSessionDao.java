/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.dao;

import fr.asipsante.platines.entity.TestSession;
import java.util.Date;
import java.util.List;

/**
 * Interface provides methods required to link {@link TestSession} entity to a data source.
 *
 * @author apierre
 */
public interface ITestSessionDao extends IGenericDao<TestSession, Long> {

  /**
   * Gets all the enabled sessions.
   *
   * @return all the enabled sessions
   */
  List<TestSession> getSessionsEnabled();

  /**
   * Gets all the disabled sessions.
   *
   * @return all the disabled sessions
   */
  List<TestSession> getSessionsDisabled();

  /**
   * Deletes a session.
   *
   * @param session, the session to delete
   * @param date, the suppression date
   */
  void deleteSession(TestSession session, Date date);

  /**
   * Duplicates a session.
   *
   * @param session, the session to duplicate
   * @return the new session
   */
  TestSession duplicateSession(TestSession session);

  /**
   * Gets a session by its uuid.
   *
   * @param uuid, the session uuid
   * @return the session
   */
  TestSession getSessionByUuid(String uuid);

  /**
   * Gets the session log as zip.
   *
   * @param idSession, the session id
   * @return the session log
   */
  byte[] getZipLogSession(Long idSession);

  /**
   * Updates a session without token.
   *
   * @param testSession, the sessio to update
   */
  void updateSessionWithoutToken(TestSession testSession);

  /**
   * Gets all active sessions.
   *
   * @return all active sessions
   */
  List<TestSession> getActiveSessions();
}
