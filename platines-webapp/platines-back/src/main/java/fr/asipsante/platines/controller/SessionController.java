/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.controller;

import fr.asipsante.platines.dto.ApplicationListDto;
import fr.asipsante.platines.dto.ProjectSessionDto;
import fr.asipsante.platines.dto.RServerOperationHistoryDto;
import fr.asipsante.platines.dto.SessionDurationDto;
import fr.asipsante.platines.dto.StepLogDto;
import fr.asipsante.platines.dto.TestSessionDetailDto;
import fr.asipsante.platines.dto.TestSessionDto;
import fr.asipsante.platines.dto.TestSessionGestionDto;
import fr.asipsante.platines.dto.TestSessionListDto;
import fr.asipsante.platines.dto.UserDto;
import fr.asipsante.platines.entity.enums.Role;
import fr.asipsante.platines.service.SessionRunner;
import fr.asipsante.platines.service.SessionService;
import fr.asipsante.platines.service.UserService;
import fr.asipsante.platines.service.impl.common.SessionRunnerImpl;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author apierre
 */
@RequestMapping("/secure")
@RestController
public class SessionController {

  /** The session Service. */
  @Autowired
  @Qualifier("sessionService")
  private SessionService sessionService;

  /** The session Runner. */
  @Autowired private SessionRunner sessionRunner;

  /** The user service. */
  @Autowired private UserService userService;

  /**
   * List all test sessions.
   *
   * @return the session list
   */
  @GetMapping(value = "/testSession/testSessionList", produces = "application/json")
  public List<TestSessionListDto> getSessionsTestList() {
    return sessionService.getAllTestSessionList();
  }

  /**
   * Gets the testSessions deleted list.
   *
   * @return the session deleted list
   */
  @GetMapping(value = "/testSession/testSessionsListDisabled", produces = "application/json")
  public List<TestSessionListDto> getSessionsTestListDisabled() {
    return sessionService.getTestSessionsListDisabled();
  }

  /**
   * Delete a session.
   *
   * @param id the id session
   */
  @DeleteMapping(value = "/testSession/delete/{id}", produces = "application/json")
  public void deleteSession(@PathVariable Long id) {
    sessionService.deleteSession(id);
  }

  /**
   * Gets a session by is id.
   *
   * @param id, the session id to return
   * @return the session
   */
  @GetMapping(value = "/testSession/session/{id}", produces = "application/json")
  public TestSessionDto getSessionById(@PathVariable final Long id) {
    return sessionService.getSessionById(id);
  }

  /**
   * Gets the applications for one user.
   *
   * @param id, the user selected
   * @return the applications associated
   */
  @GetMapping(value = "/testSession/applications/{id}", produces = "application/json")
  public List<ApplicationListDto> getUserApplications(@PathVariable Long id) {
    return sessionService.getUserApplications(id);
  }

  /**
   * List all the duration enable for a testSession.
   *
   * @return the list of sessionDurations
   */
  @GetMapping(value = "/testSession/durations", produces = "application/json")
  public List<SessionDurationDto> listTestSessionDurations() {
    return sessionService.getSessionDurations();
  }

  /**
   * Gets the projects for a version and a applicationRole.
   *
   * @param idVersion the version id
   * @param role the application role
   * @param token the user
   * @return the list of projects for the version and the application role.
   */
  @GetMapping(value = "/testSession/projects/{idVersion}/{role}", produces = "application/json")
  public List<ProjectSessionDto> getProjects(
      @PathVariable Long idVersion,
      @PathVariable String role,
      @RequestHeader(value = "Authorization") String token) {
    final Role appRole = Role.valueOf(role);
    return sessionService.getUserProjects(appRole, idVersion);
  }

  /**
   * Create a testSession.
   *
   * @param session, the TestSession to persist
   */
  @PostMapping(value = "/testSession/create", produces = "application/json")
  public void createSessionTst(@RequestBody final TestSessionDto session) {
    sessionService.createSession(session);
  }

  /**
   * Update a testSession.
   *
   * @param session, the testSession to update
   */
  @PostMapping(value = "/testSession/update", produces = "application/json")
  public void updateSession(@RequestBody TestSessionDto session) {
    sessionService.updateSession(session);
  }

  /**
   * Restore a testSession.
   *
   * @param session, the testSession to restore
   */
  @PostMapping(value = "/testSession/restore", produces = "application/json")
  public void restoreSession(@RequestBody TestSessionDto session) {
    sessionService.restoreSession(session);
  }

  /**
   * Duplicate a testSession.
   *
   * @param token, the user who is doing the duplication.
   * @param session, the testSession to duplicate
   */
  @PostMapping(value = "/testSession/duplicate", produces = "application/json")
  public void duplicateSession(
      @RequestHeader(value = "Authorization") String token, @RequestBody TestSessionDto session) {
    session.setUuid(null);
    session.setCreationDate(null);
    session.setSessionStatus(null);
    final UserDto user = userService.getUserByToken(token);
    sessionService.duplicateSession(session, user);
  }

  /**
   * Gets the details of a session.
   *
   * @param id, the session id
   * @return the session details
   */
  @GetMapping(value = "/testSession/detail/{id}", produces = "application/json")
  public TestSessionDetailDto getSessionDetail(@PathVariable Long id) {
    return sessionService.getSessionDetail(id);
  }

  /**
   * Gets the errors for a stepCase by the id.
   *
   * @param id, the stepCase id
   * @return the logs for the stepCase
   */
  @GetMapping(value = "/testSession/logs/{id}", produces = "application/json")
  public StepLogDto getStepErrorById(@PathVariable Long id) {
    return sessionService.getErrorById(id);
  }

  /**
   * Gets a zip with the related files and the project.
   *
   * @param idSession, the project id
   * @return a zip, contains the project and his related files.
   */
  @GetMapping(produces = "application/zip", value = "testSession/logSession/{idSession}")
  public byte[] getLogSession(@PathVariable Long idSession) {

    return sessionService.getZipLogSession(idSession).getZip();
  }

  /**
   * Execute a testSession.
   *
   * <p>FIXME : get void ? really ? This rapes GET semantics and creates SNAFUs in the client.
   * https://tickets.forge.ans.henix.fr/redmine/issues/58745
   *
   * @param id, the session to execute
   */
  @GetMapping(value = "/testSession/execute/{id}", produces = "application/json")
  public void executeSession(@PathVariable Long id) {
    final TestSessionDto sessionDto = sessionService.getSessionById(id);
    sessionService.launchExecutionSession(sessionDto);
  }

  /**
   * override by spring.
   *
   * @return session manager
   */
  @Lookup
  public SessionRunnerImpl getPrototypeBean() {
    // spring will override this method
    return null;
  }

  /**
   * Gets all the session with status : waiting, construct, deployment or pending.
   *
   * @return the active sessions list
   */
  @PreAuthorize("hasRole('admin') or hasRole('manager')")
  @GetMapping(value = "/testSession/allActive", produces = "application/json")
  public List<TestSessionGestionDto> getActiveSession() {
    return sessionService.getActiveSessions();
  }

  /**
   * Stop the job nomad associated to the session.
   *
   * @param id, the session id to stop.
   */
  @PreAuthorize("hasRole('admin') or hasRole('manager')")
  @GetMapping(value = "/testSession/stop/{id}", produces = "application/json")
  public void stopSession(@PathVariable Long id) {
    sessionRunner.stop(id);
  }

  /**
   * Suppress definitively from the database a session.
   *
   * @param id, the session id to suppress.
   */
  @DeleteMapping(value = "/testSession/suppress/{id}", produces = "application/json")
  public void suppressSession(@PathVariable Long id) {
    sessionService.suppressSession(id);
  }

  /**
   * Gets the projects results for a session server.
   *
   * @param idSession, the session id
   * @return the projects results
   */
  @GetMapping(value = "/testSession/rOperation/{idSession}", produces = "application/json")
  public List<RServerOperationHistoryDto> getProjectResultsServerSession(
      @PathVariable Long idSession) {
    return sessionService.getOperationBySession(idSession);
  }

  /**
   * Gets logs for a session server.
   *
   * @param id, the operation id
   * @return the logs
   */
  @GetMapping(value = "/testSession/logsServer/{id}", produces = "application/json")
  public StepLogDto getStepLogServerById(@PathVariable Long id) {
    return sessionService.getRequestResponseByROperation(id);
  }
}
