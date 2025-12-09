/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.service;

import fr.asipsante.platines.dto.ApplicationListDto;
import fr.asipsante.platines.dto.ProjectSessionDto;
import fr.asipsante.platines.dto.RServerOperationHistoryDto;
import fr.asipsante.platines.dto.ServerOperation;
import fr.asipsante.platines.dto.SessionDurationDto;
import fr.asipsante.platines.dto.StepLogDto;
import fr.asipsante.platines.dto.TestSessionDetailDto;
import fr.asipsante.platines.dto.TestSessionDto;
import fr.asipsante.platines.dto.TestSessionGestionDto;
import fr.asipsante.platines.dto.TestSessionListDto;
import fr.asipsante.platines.dto.UserDto;
import fr.asipsante.platines.dto.ZipDto;
import fr.asipsante.platines.entity.enums.Role;
import fr.asipsante.platines.model.DriverSessionResult;
import fr.asipsante.platines.model.DriverTestResult;
import java.util.List;

/**
 * The Session service.
 *
 * @author apierre
 */
public interface SessionService {

  /**
   * Gets all test sessions register in database.
   *
   * @return all the test sessions DTO.
   */
  List<TestSessionListDto> getAllTestSessionList();

  /**
   * Gets all test sessions register in database and disabled.
   *
   * @return all the sessions disabled.
   */
  List<TestSessionListDto> getTestSessionsListDisabled();

  /**
   * Put the session in trash. Set the suppression date.
   *
   * @param id the id
   */
  void deleteSession(Long id);

  /**
   * Gets a session by is id.
   *
   * @param id the id
   * @return the session corresponding to the id
   */
  TestSessionDto getSessionById(Long id);

  /**
   * Gets all the application for a user.
   *
   * @param id the id
   * @return all the user applications.
   */
  List<ApplicationListDto> getUserApplications(Long id);

  /**
   * Gets all the session durations.
   *
   * @return the session durations.
   */
  List<SessionDurationDto> getSessionDurations();

  /**
   * Gets the projects corresponding to the application role, and the version for a user.
   *
   * @param appRole the app role
   * @param idVersion the id version
   * @return all the projects corresponding
   */
  List<ProjectSessionDto> getUserProjects(Role appRole, Long idVersion);

  /**
   * Persists a session.
   *
   * @param sessionDto the session dto
   */
  void createSession(TestSessionDto sessionDto);

  /**
   * Updates a session.
   *
   * @param sessionDto the session dto
   */
  void updateSession(TestSessionDto sessionDto);

  /**
   * Restore a session.
   *
   * @param sessionDto the session dto
   */
  void restoreSession(TestSessionDto sessionDto);

  /**
   * Duplicates a session.
   *
   * @param session the session to duplicate
   * @param user the user
   */
  void duplicateSession(TestSessionDto session, UserDto user);

  /**
   * Changes the session status.
   *
   * @param driverResult the driver result
   */
  void updateStatusSession(DriverSessionResult driverResult);

  /**
   * Uploads the session log.
   *
   * @param uuidSession the uuid session
   * @param logs the logs
   */
  void uploadLogSession(String uuidSession, byte[] logs);

  /**
   * Updates the session projectResult.
   *
   * @param driverTestResult the driver test result
   */
  void updateSessionProject(DriverTestResult driverTestResult);

  /**
   * Gets the detail of a session.
   *
   * @param id the id
   * @return the details of the session
   */
  TestSessionDetailDto getSessionDetail(Long id);

  /**
   * Gets the error log for a session.
   *
   * @param id the id
   * @return the error log
   */
  StepLogDto getErrorById(Long id);

  /**
   * Gets the logs of the session as a zip.
   *
   * @param idSession the id session
   * @return a zip with the session logs
   */
  ZipDto getZipLogSession(Long idSession);

  /**
   * Initializes the execution of a session. Set the sessions status to WAITING.
   *
   * @param idSession the id session
   */
  void initExecutionSession(Long idSession);

  /**
   * Launchs the session execution. Initializes the session manager.
   *
   * @param idSession the id session
   */
  void launchExecutionSession(TestSessionDto idSession);

  /**
   * Monitors the session execution.
   *
   * @param sessionRunner the session manager
   */
  void monitorExecutionSession(SessionRunner sessionRunner);

  /**
   * Gets all the active sessions. It is sessions with status Construct, deployment, pending or
   * waiting.
   *
   * @return the list of active sessions
   */
  List<TestSessionGestionDto> getActiveSessions();

  /**
   * Save the session server operation in history.
   *
   * @param serverOperation the server operation
   */
  void saveSessionServerHistory(ServerOperation serverOperation);

  /**
   * Suppress definitively a session from the database.
   *
   * @param idSession the id session
   */
  void suppressSession(Long idSession);

  /**
   * Gets an operation by its id.
   *
   * @param idSession the id session
   * @return the server operation
   */
  List<RServerOperationHistoryDto> getOperationBySession(Long idSession);

  /**
   * Gets request and response by the operation id.
   *
   * @param idROperation the id r operation
   * @return request and response
   */
  StepLogDto getRequestResponseByROperation(Long idROperation);
}
