/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.service.impl.common;

import fr.asipsante.platines.dao.IApplicationDao;
import fr.asipsante.platines.dao.IProjectDao;
import fr.asipsante.platines.dao.IProjectResultDao;
import fr.asipsante.platines.dao.IProjectResultPropertyDao;
import fr.asipsante.platines.dao.IROperationExpectedDao;
import fr.asipsante.platines.dao.IROperationUnexpectedDao;
import fr.asipsante.platines.dao.ISessionDurationDao;
import fr.asipsante.platines.dao.ITestCaseDao;
import fr.asipsante.platines.dao.ITestSessionDao;
import fr.asipsante.platines.dao.ITestStepDao;
import fr.asipsante.platines.dao.ITestSuiteDao;
import fr.asipsante.platines.dto.ApplicationListDto;
import fr.asipsante.platines.dto.ProjectResultDetailDto;
import fr.asipsante.platines.dto.ProjectResultDto;
import fr.asipsante.platines.dto.ProjectResultPropertyDto;
import fr.asipsante.platines.dto.ProjectSessionDto;
import fr.asipsante.platines.dto.RServerOperationHistoryDto;
import fr.asipsante.platines.dto.ServerOperation;
import fr.asipsante.platines.dto.SessionDurationDto;
import fr.asipsante.platines.dto.SessionParameters;
import fr.asipsante.platines.dto.StepLogDto;
import fr.asipsante.platines.dto.TestSessionDetailDto;
import fr.asipsante.platines.dto.TestSessionDto;
import fr.asipsante.platines.dto.TestSessionGestionDto;
import fr.asipsante.platines.dto.TestSessionListDto;
import fr.asipsante.platines.dto.UserDto;
import fr.asipsante.platines.dto.ZipDto;
import fr.asipsante.platines.entity.Application;
import fr.asipsante.platines.entity.Project;
import fr.asipsante.platines.entity.ProjectResult;
import fr.asipsante.platines.entity.ProjectResultProperty;
import fr.asipsante.platines.entity.ROperationExpected;
import fr.asipsante.platines.entity.ROperationUnexpected;
import fr.asipsante.platines.entity.SessionDuration;
import fr.asipsante.platines.entity.TestCaseResult;
import fr.asipsante.platines.entity.TestSession;
import fr.asipsante.platines.entity.TestStepResult;
import fr.asipsante.platines.entity.TestSuiteResult;
import fr.asipsante.platines.entity.enums.ResultStatus;
import fr.asipsante.platines.entity.enums.Role;
import fr.asipsante.platines.entity.enums.SessionStatus;
import fr.asipsante.platines.executor.ProjectBuilder;
import fr.asipsante.platines.model.DriverSessionResult;
import fr.asipsante.platines.model.DriverTestCaseResult;
import fr.asipsante.platines.model.DriverTestResult;
import fr.asipsante.platines.model.DriverTestStepResult;
import fr.asipsante.platines.model.DriverTestSuiteResult;
import fr.asipsante.platines.service.SessionRunner;
import fr.asipsante.platines.service.SessionService;
import fr.asipsante.platines.service.mapper.ApplicationDtoMapper;
import fr.asipsante.platines.service.mapper.ProjectDtoMapper;
import fr.asipsante.platines.service.mapper.ProjectResultDtoMapper;
import fr.asipsante.platines.service.mapper.SessionDurationDtoMapper;
import fr.asipsante.platines.service.mapper.TestSessionDtoMapper;
import fr.asipsante.platines.service.mapper.TestStepResultDtoMapper;
import fr.asipsante.platines.service.mapper.VersionDtoMapper;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author apierre
 */
@Service(value = "sessionService")
public class SessionServiceImpl implements SessionService {

  private static final String EXTERNAL_PROTOCOL = "https";

  @Value("${mockservices.domain}")
  private String MOCK_SERVICE_DOMAIN;

  /** Logger. */
  private static final Logger LOGGER = LoggerFactory.getLogger(SessionServiceImpl.class);

  @Autowired private SessionRunner sessionRunner;

  /** TestSessionDao. */
  @Autowired
  @Qualifier("testSessionDao")
  private ITestSessionDao sessionDao;

  /** ApplicationDao. */
  @Autowired
  @Qualifier("applicationDao")
  private IApplicationDao applicationDao;

  /** Duration SessionDao. */
  @Autowired
  @Qualifier("sessionDurationDao")
  private ISessionDurationDao sessionDurationDao;

  /** ProjectResultDao. */
  @Autowired
  @Qualifier("projectResultDao")
  private IProjectResultDao projectResultDao;

  /** ProjectResultPropertyDao. */
  @Autowired
  @Qualifier("projectResultPropertyDao")
  private IProjectResultPropertyDao projectResultPropertyDao;

  /** ITestSuiteDao. */
  @Autowired
  @Qualifier("testSuiteDao")
  private ITestSuiteDao testSuiteDao;

  /** ITestCaseDao. */
  @Autowired
  @Qualifier("testCaseDao")
  private ITestCaseDao testCaseDao;

  /** ITestStepDao. */
  @Autowired
  @Qualifier("testStepDao")
  private ITestStepDao testStepDao;

  /** IProjectDao. */
  @Autowired private IProjectDao projectDao;

  /** IROperationExpectedDao. */
  @Autowired
  @Qualifier("rOperationExpected")
  private IROperationExpectedDao rOperationExpected;

  /** IROperationUnexpectedDao. */
  @Autowired
  @Qualifier("rOperationUnexpected")
  private IROperationUnexpectedDao rOperationUnexpected;

  /** Entity - DTO converters. */
  @Autowired
  @Qualifier("testSessionDtoMapper")
  private TestSessionDtoMapper testSessionDtoMapper;

  @Autowired
  @Qualifier("applicationDtoMapper")
  private ApplicationDtoMapper applicationDtoMapper;

  @Autowired
  @Qualifier("sessionDurationDtoMapper")
  private SessionDurationDtoMapper sessionDurationDtoMapper;

  @Autowired
  @Qualifier("projectDtoMapper")
  private ProjectDtoMapper projectDtoMapper;

  @Autowired
  @Qualifier("versionDtoMapper")
  private VersionDtoMapper versionDtoMapper;

  @Autowired
  @Qualifier("testStepResultDtoMapper")
  private TestStepResultDtoMapper testStepResultDtoMapper;

  @Autowired
  @Qualifier("projectResultDtoMapper")
  private ProjectResultDtoMapper projectResultDtoMapper;

  /** DateConverter. */
  @Autowired private DateConverter dateConverter;

  /** SessionStateManager. */
  @Autowired private SessionStateManager manager;

  @Autowired private ProjectBuilder projectBuilder;

  /** {@inheritDoc} */
  @Override
  @Transactional(readOnly = true)
  public List<TestSessionListDto> getAllTestSessionList() {
    final List<TestSession> testSessions = sessionDao.getSessionsEnabled();
    final List<TestSessionListDto> testSessionsDto = new ArrayList<>();
    testSessions.forEach(
        testSession ->
            testSessionsDto.add(testSessionDtoMapper.convertToSessionListDto(testSession)));
    return testSessionsDto;
  }

  /** {@inheritDoc} */
  @Override
  @Transactional(readOnly = true)
  public List<TestSessionListDto> getTestSessionsListDisabled() {
    final List<TestSession> testSessions = sessionDao.getSessionsDisabled();
    final List<TestSessionListDto> testSessionsDto = new ArrayList<>();
    testSessions.forEach(
        testSession ->
            testSessionsDto.add(testSessionDtoMapper.convertToSessionListDto(testSession)));
    return testSessionsDto;
  }

  /** {@inheritDoc} */
  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public void deleteSession(Long id) {
    final Date dateSuppression = dateConverter.convertToUTC(new Date());
    final TestSession session = sessionDao.getById(id);
    sessionDao.deleteSession(session, dateSuppression);
  }

  /** {@inheritDoc} */
  @Override
  @Transactional(readOnly = true)
  public TestSessionDto getSessionById(Long id) {
    return testSessionDtoMapper.convertToSessionDto(sessionDao.getById(id));
  }

  /** {@inheritDoc} */
  @Override
  @Transactional(readOnly = true)
  public List<ApplicationListDto> getUserApplications(Long id) {
    final List<ApplicationListDto> applicationsDto = new ArrayList<>();
    final List<Application> applications = applicationDao.getApplicationsByUser(id);
    applications.forEach(
        application ->
            applicationsDto.add(applicationDtoMapper.convertToApplicationListDto(application)));
    return applicationsDto;
  }

  /** {@inheritDoc} */
  @Override
  @Transactional(readOnly = true)
  public List<SessionDurationDto> getSessionDurations() {
    final List<SessionDurationDto> durationsDto = new ArrayList<>();
    final List<SessionDuration> durations = sessionDurationDao.getAllDurations();
    durations.forEach(
        duration -> durationsDto.add(sessionDurationDtoMapper.convertToDto(duration)));
    return durationsDto;
  }

  /** {@inheritDoc} */
  @Override
  @Transactional(readOnly = true)
  public List<ProjectSessionDto> getUserProjects(Role appRole, Long idVersion) {
    final List<ProjectSessionDto> projectsDto = new ArrayList<>();
    final List<Project> projects = projectDao.getProjectsByVersion(idVersion);
    projects.forEach(
        project -> {
          if (!project.getRole().getValue().equals(appRole.getValue())) {
            projectsDto.add(projectDtoMapper.convertToProjectSessionDto(project));
          }
        });
    return projectsDto;
  }

  /** {@inheritDoc} */
  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public void createSession(TestSessionDto sessionDto) {
    final TestSession session = testSessionDtoMapper.convertToTestSession(sessionDto);
    saveSession(session);
  }

  /** {@inheritDoc} */
  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public void updateSession(TestSessionDto sessionDto) {
    final TestSession session = sessionDao.getById(sessionDto.getId());

    session.setApplication(applicationDtoMapper.convertToApplication(sessionDto.getApplication()));
    session.setVersion(versionDtoMapper.convertToVersion(sessionDto.getVersion()));
    session.setDescription(sessionDto.getDescription());
    if (sessionDto.getSessionDuration() != null) {
      session.setSessionDuration(
          sessionDurationDtoMapper.convertToEntity(sessionDto.getSessionDuration()));
    }
    session
        .getProjectResults()
        .forEach(
            projet -> {
              if (sessionDto.getProjectResults().isEmpty()) {
                projectResultDao.delete(projet);
              } else {
                final Predicate<ProjectResultDto> pred =
                    p -> p.getId().equals(projet.getId()); // pNew = pBase
                final Predicate<ProjectResultDto> pred2 =
                    p -> p.getId() == null; // pNew = null -> nouveau
                if (sessionDto.getProjectResults().stream()
                    .noneMatch(pred2)) { // pas nouveau projet
                  if (sessionDto.getProjectResults().stream().anyMatch(pred)) { // existe deja
                    final ProjectResultDto projectResultDto =
                        sessionDto.getProjectResults().stream().filter(pred).findFirst().get();
                    projet.setNumberOrder(projectResultDto.getNumberOrder());
                    projet
                        .getProjectProperties()
                        .forEach(
                            prop -> {
                              final Predicate<ProjectResultPropertyDto> predProperty =
                                  p -> p.getKey().equals(prop.getKey());
                              final ProjectResultPropertyDto projectResultPropertyDto =
                                  projectResultDto.getProjectProperties().stream()
                                      .filter(predProperty)
                                      .findFirst()
                                      .get();
                              prop.setValue(projectResultPropertyDto.getValue());
                            });
                  } else if (sessionDto.getProjectResults().stream().noneMatch(pred2)) {
                    projectResultDao.delete(projet);
                  }
                } else {
                  final List<ProjectResultDto> projectsDto = new ArrayList<>();
                  sessionDto
                      .getProjectResults()
                      .forEach(
                          p -> {
                            if (p.getId() != null) {
                              projectsDto.add(p);
                            }
                          });
                  if (projectsDto.stream().anyMatch(pred)) {
                    final ProjectResultDto projectResultDto =
                        projectsDto.stream().filter(pred).findFirst().get();
                    projet.setNumberOrder(projectResultDto.getNumberOrder());
                    projet
                        .getProjectProperties()
                        .forEach(
                            prop -> {
                              final Predicate<ProjectResultPropertyDto> predProperty =
                                  p -> p.getKey().equals(prop.getKey());
                              final ProjectResultPropertyDto projectResultPropertyDto =
                                  projectResultDto.getProjectProperties().stream()
                                      .filter(predProperty)
                                      .findFirst()
                                      .get();
                              prop.setValue(projectResultPropertyDto.getValue());
                            });
                  } else if (projectsDto.stream().noneMatch(pred2)) {
                    projectResultDao.delete(projet);
                  }
                }
              }
            });
    sessionDto
        .getProjectResults()
        .forEach(
            projectDto -> {
              if (projectDto.getId() == null) {
                ProjectResult projectResult =
                    projectResultDtoMapper.convertToProjectResult(projectDto);
                projectResult.setTestSession(session);
                final List<ProjectResultProperty> projectResultProperties =
                    projectResult.getProjectProperties();
                projectResult.setProjectProperties(new ArrayList<>());
                projectResult.setDescription(
                    projectDao.getById(projectResult.getIdProject()).getDescription());
                projectResult = projectResultDao.saveAndGet(projectResult);
                for (ProjectResultProperty projectResultProperty : projectResultProperties) {
                  projectResultProperty.setProjectResult(projectResult);
                  projectResultProperty =
                      projectResultPropertyDao.saveAndGet(projectResultProperty);
                  projectResult.getProjectProperties().add(projectResultProperty);
                }
                session.getProjectResults().add(projectResult);
              }
            });
  }

  /** {@inheritDoc} */
  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public void restoreSession(TestSessionDto sessionDto) {
    final TestSession session = sessionDao.getById(sessionDto.getId());
    session.setSuppressionDate(null);
  }

  /** {@inheritDoc} */
  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public void duplicateSession(TestSessionDto sessionDto, UserDto user) {
    sessionDto.setId(null);
    final TestSession session = testSessionDtoMapper.convertToTestSession(sessionDto);
    saveSession(session);
  }

  /** {@inheritDoc} */
  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public void updateStatusSession(DriverSessionResult driverResult) {

    final TestSession sessionTst = sessionDao.getSessionByUuid(driverResult.getUuidSession());
    if (driverResult.getDateExecution() != null) {
      sessionTst.setExecutionDate(dateConverter.convertToUTC(driverResult.getDateExecution()));
    }
    sessionTst.setSessionStatus(SessionStatus.valueOf(driverResult.getStatus()));
    sessionTst.setExecutionDuration(driverResult.getTimeTaken());
    sessionDao.updateSessionWithoutToken(sessionTst);
    manager.updateAndPublishSessionStatus(
        sessionTst.getId(), SessionStatus.valueOf(driverResult.getStatus()));
  }

  /** {@inheritDoc} */
  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public void uploadLogSession(String uuidSession, byte[] logs) {
    final TestSession session = sessionDao.getSessionByUuid(uuidSession);
    session.setLog(logs);
    if (!SessionStatus.FINISHED.equals(session.getSessionStatus())) {
      session.setSessionStatus(SessionStatus.FINISHED);
    }
    sessionDao.updateSessionWithoutToken(session);
  }

  /** {@inheritDoc} */
  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public void updateSessionProject(DriverTestResult driverTestResult) {
    final ProjectResult projectR =
        projectResultDao.getByIdWithoutToken(Long.parseLong(driverTestResult.getProjectId()));
    projectR.setExecutionDate(dateConverter.convertToUTC(driverTestResult.getDateExecution()));
    projectR.setDescription(driverTestResult.getDescription());
    projectR.setResultStatus(ResultStatus.valueOf(driverTestResult.getStatus()));
    projectR.setExecutionDuration(driverTestResult.getExecutionTime());
    projectResultDao.updateProjectResultWithoutToken(projectR);

    for (final DriverTestSuiteResult driverTestSuiteResult : driverTestResult.getSuiteResults()) {

      TestSuiteResult suiteR = new TestSuiteResult();
      suiteR.setName(driverTestSuiteResult.getName());
      suiteR.setResultStatus(ResultStatus.valueOf(driverTestSuiteResult.getStatus()));
      suiteR.setExecutionDate(dateConverter.convertToUTC(driverTestSuiteResult.getDateExecution()));
      suiteR.setDescription(driverTestSuiteResult.getDescription());
      suiteR.setExecutionDuration(driverTestSuiteResult.getExecutionTime());
      suiteR.setProjectResult(projectR);
      suiteR = testSuiteDao.persistAndGet(suiteR);
      for (final DriverTestCaseResult driverTestCaseResult :
          driverTestSuiteResult.getCaseResults()) {
        TestCaseResult caseR = new TestCaseResult();
        caseR.setName(driverTestCaseResult.getName());
        caseR.setResultStatus(ResultStatus.valueOf(driverTestCaseResult.getStatus()));
        caseR.setExecutionDate(dateConverter.convertToUTC(driverTestCaseResult.getDateExecution()));
        caseR.setDescription(driverTestCaseResult.getDescription());
        caseR.setExecutionDuration(driverTestCaseResult.getExecutionTime());
        caseR.setCriticality(driverTestCaseResult.getCriticality());
        caseR.setTestSuite(suiteR);
        caseR = testCaseDao.persistAndGet(caseR);
        for (final DriverTestStepResult driverTestStepResult :
            driverTestCaseResult.getStepResults()) {
          final TestStepResult stepR = new TestStepResult();
          stepR.setName(driverTestStepResult.getName());
          stepR.setStatus(ResultStatus.valueOf(driverTestStepResult.getStatus()));
          stepR.setExecutionDate(
              dateConverter.convertToUTC(driverTestStepResult.getDateExecution()));
          stepR.setDescription(driverTestStepResult.getDescription());
          stepR.setExecutionDuration(driverTestStepResult.getExecutionTime());
          stepR.setError(
              driverTestStepResult.getErrors().stream()
                  .map(Object::toString)
                  .collect(Collectors.joining("\n")));
          stepR.setRequest(driverTestStepResult.getRequest());
          stepR.setResponse(driverTestStepResult.getResponse());
          stepR.setTestCase(caseR);
          testStepDao.persistWithoutToken(stepR);
        }
      }
    }
    manager.publishStateOfProjectResults(projectR.getId(), driverTestResult.getStatus());
  }

  /** {@inheritDoc} */
  @Override
  @Transactional(readOnly = true)
  public TestSessionDetailDto getSessionDetail(Long id) {
    TestSession session = sessionDao.getById(id);
    final TestSessionDetailDto sessionDetailDto =
        testSessionDtoMapper.convertToTestSessionDetailDto(session);
    final List<String> contexts = new ArrayList<String>();
    // Pour les mockservice, on ajoute les contextes et l'url du mock
    if (session.getApplication().getRole().equals(Role.CLIENT)) {
      final String url =
          EXTERNAL_PROTOCOL
              + "://"
              + sessionDetailDto.getUuid()
              + "."
              + MOCK_SERVICE_DOMAIN
              + "/mockservice";
      List<ProjectResultDetailDto> projectsResultDetails = sessionDetailDto.getProjectResults();
      for (ProjectResultDetailDto projectResultDetailDto : projectsResultDetails) {
        Project project = projectDao.getById(projectResultDetailDto.getIdProject());
        contexts.addAll(projectBuilder.getMockContextPath(project.getFile()));
      }
      sessionDetailDto.setUrl(url);
    }
    sessionDetailDto.setResourcePath(contexts);
    return sessionDetailDto;
  }

  /** {@inheritDoc} */
  @Override
  @Transactional(readOnly = true)
  public StepLogDto getErrorById(Long id) {
    return testStepResultDtoMapper.convertToStepLogDto(testStepDao.getById(id));
  }

  /** {@inheritDoc} */
  @Override
  @Transactional(readOnly = true)
  public ZipDto getZipLogSession(Long idSession) {
    ZipDto zipDto = new ZipDto(null);
    byte[] zip = sessionDao.getZipLogSession(idSession);
    if (zip != null) {
      zipDto = new ZipDto(zip);
    }
    return zipDto;
  }

  /** {@inheritDoc} */
  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public void initExecutionSession(Long id) {
    final TestSession session = sessionDao.getById(id);
    session.setSessionStatus(SessionStatus.WAITING);
    sessionDao.update(session);
    manager.updateAndPublishSessionStatus(id, SessionStatus.WAITING);
  }

  /** {@inheritDoc} */
  @Override
  public void launchExecutionSession(TestSessionDto testSession) {
    sessionRunner.setSessionTst(testSession);
    final SessionParameters params = sessionRunner.init();
    if (params != null) {
      sessionRunner.launch();
    }
  }

  /** {@inheritDoc} */
  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public void monitorExecutionSession(SessionRunner sessionRunner) {
    // does nothing for now
  }

  /** {@inheritDoc} */
  @Override
  @Transactional
  public List<TestSessionGestionDto> getActiveSessions() {
    final List<TestSession> sessions = sessionDao.getActiveSessions();
    final List<TestSessionGestionDto> sessionsDto = new ArrayList<>();
    sessions.forEach(
        session -> sessionsDto.add(testSessionDtoMapper.convertToSessionGestionDto(session)));
    return sessionsDto;
  }

  /** {@inheritDoc} */
  @Transactional(propagation = Propagation.REQUIRED)
  @Override
  public void saveSessionServerHistory(ServerOperation serverOperation) {
    final List<TestCaseResult> testCaseResult =
        testCaseDao.getTestCasesByUuidSession(serverOperation.getUuidSession());
    if (serverOperation.getResponseName() != null) {
      final Predicate<TestCaseResult> pred =
          p -> serverOperation.getResponseName().equals(p.getResponseName());
      final List<TestCaseResult> predCaseResult =
          testCaseResult.stream().filter(pred).collect(Collectors.toList());
      if (!predCaseResult.isEmpty()) {
        predCaseResult.forEach(
            c -> {
              final ROperationExpected operationExpected = new ROperationExpected();
              operationExpected.setOperationDate(
                  dateConverter.convertToUTC(serverOperation.getOperationDate()));
              operationExpected.setRequest(serverOperation.getRequest());
              operationExpected.setResponse(serverOperation.getResponse());
              operationExpected.setResponseName(serverOperation.getResponseName());
              operationExpected.setTimeTaken(serverOperation.getTimeTaken());
              operationExpected.setOperation(serverOperation.getOperation());
              operationExpected.setTestCaseResult(c);
              rOperationExpected.persist(operationExpected);
              c.setResultStatus(ResultStatus.SUCCESS);
              testCaseDao.updateTestCase(c);
              updateStatusRProjet(c);
            });
      } else {
        createROperationUnexpected(serverOperation);
      }
    } else {
      createROperationUnexpected(serverOperation);
    }
    manager.publishSessionState(serverOperation.getUuidSession());
  }

  /** {@inheritDoc} */
  @Override
  @Transactional
  public void suppressSession(Long idSession) {
    final TestSession session = sessionDao.getById(idSession);
    sessionDao.delete(session);
  }

  /** {@inheritDoc} */
  @Transactional(propagation = Propagation.REQUIRED)
  @Override
  public List<RServerOperationHistoryDto> getOperationBySession(Long idSession) {
    final List<ROperationExpected> expectedList =
        rOperationExpected.getROperationExpectedBySession(idSession);
    final List<ROperationUnexpected> unexpectedList =
        rOperationUnexpected.getROperationsBySession(idSession);
    final List<RServerOperationHistoryDto> historyDtos = new ArrayList<>();
    expectedList.forEach(
        expect -> {
          final RServerOperationHistoryDto operationHistoryDto = new RServerOperationHistoryDto();
          operationHistoryDto.setOperation(expect.getOperation());
          operationHistoryDto.setOperationDate(expect.getOperationDate());
          operationHistoryDto.setResponseName(expect.getResponseName());
          operationHistoryDto.setTimeTaken(expect.getTimeTaken());
          historyDtos.add(operationHistoryDto);
        });
    unexpectedList.forEach(
        unexpect -> {
          final RServerOperationHistoryDto operationHistoryDto = new RServerOperationHistoryDto();
          operationHistoryDto.setOperation(unexpect.getOperation());
          operationHistoryDto.setOperationDate(unexpect.getOperationDate());
          operationHistoryDto.setResponseName(unexpect.getResponseName());
          operationHistoryDto.setTimeTaken(unexpect.getTimeTaken());
          historyDtos.add(operationHistoryDto);
        });

    historyDtos.sort(Comparator.comparing(RServerOperationHistoryDto::getOperationDate));
    return historyDtos;
  }

  /** {@inheritDoc} */
  @Transactional(propagation = Propagation.REQUIRED)
  @Override
  public StepLogDto getRequestResponseByROperation(Long idROperation) {

    final ROperationExpected expect = rOperationExpected.getROperationExpectedById(idROperation);
    final StepLogDto logDto = new StepLogDto();
    logDto.setRequest(expect.getRequest());
    logDto.setResponse(expect.getResponse());
    return logDto;
  }

  private Long saveSession(TestSession testSession) {
    final List<ProjectResult> projectResults = testSession.getProjectResults();
    testSession.setProjectResults(new ArrayList<>());
    testSession.setUuid(UUID.randomUUID().toString());
    testSession.setCreationDate(dateConverter.convertToUTC(new Date()));
    testSession.setSessionStatus(SessionStatus.NONEXECUTE);
    final TestSession testSessionSave = sessionDao.saveAndGet(testSession);
    for (ProjectResult projectResult : projectResults) {
      projectResult.setTestSession(testSessionSave);
      projectResult.setDescription(
          projectDao.getById(projectResult.getIdProject()).getDescription());
      if (projectResult.getProjectProperties() != null) {
        final List<ProjectResultProperty> projectResultProperties =
            projectResult.getProjectProperties();
        projectResult.setProjectProperties(new ArrayList<>());
        projectResult.setResultStatus(ResultStatus.NONEXECUTE);
        projectResult = projectResultDao.saveAndGet(projectResult);
        for (final ProjectResultProperty projectResultProperty : projectResultProperties) {
          projectResultProperty.setProjectResult(projectResult);
          projectResultPropertyDao.saveAndGet(projectResultProperty);
          projectResult.getProjectProperties().add(projectResultProperty);
        }
      }
      testSessionSave.getProjectResults().add(projectResult);
    }
    return testSessionSave.getId();
  }

  private void updateStatusRProjet(TestCaseResult caseResult) {
    final TestSuiteResult testSuiteResult = caseResult.getTestSuite();
    final List<TestCaseResult> caseResults =
        testCaseDao.getTestCasesByTestSuite(testSuiteResult.getId());
    final Predicate<TestCaseResult> pred = p -> p.getResultStatus() == ResultStatus.NONEXECUTE;
    if (caseResults.stream().noneMatch(pred)) {
      testSuiteResult.setResultStatus(ResultStatus.SUCCESS);
      testSuiteDao.updateTestSuite(testSuiteResult);
      final List<TestSuiteResult> suiteResults =
          testSuiteDao.getTestSuiteByRproject(testSuiteResult.getProjectResult().getId());
      final Predicate<TestSuiteResult> predSuite =
          p -> p.getResultStatus() == ResultStatus.NONEXECUTE;
      if (suiteResults.stream().noneMatch(predSuite)) {
        final ProjectResult projectResult =
            projectResultDao.getByIdWithoutToken(testSuiteResult.getProjectResult().getId());
        projectResult.setResultStatus(ResultStatus.SUCCESS);
        projectResultDao.updateProjectResultWithoutToken(projectResult);
      }
    }
  }

  private void createROperationUnexpected(ServerOperation serverOperation) {
    final ROperationUnexpected operationUnexpected = new ROperationUnexpected();
    operationUnexpected.setOperationDate(
        dateConverter.convertToUTC(serverOperation.getOperationDate()));
    operationUnexpected.setRequest(serverOperation.getRequest());
    operationUnexpected.setResponse(serverOperation.getResponse());
    operationUnexpected.setResponseName(serverOperation.getResponseName());
    operationUnexpected.setTimeTaken(serverOperation.getTimeTaken());
    operationUnexpected.setOperation(serverOperation.getOperation());
    operationUnexpected.setTestSession(
        sessionDao.getSessionByUuid(serverOperation.getUuidSession()));
    rOperationUnexpected.persist(operationUnexpected);
  }
}
