/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.service.mapper;

import fr.asipsante.platines.dto.TestSessionDetailDto;
import fr.asipsante.platines.dto.TestSessionDto;
import fr.asipsante.platines.dto.TestSessionGestionDto;
import fr.asipsante.platines.dto.TestSessionListDto;
import fr.asipsante.platines.entity.TestSession;
import java.util.ArrayList;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * @author cnader
 */
@Service(value = "testSessionDtoMapper")
public class TestSessionDtoMapper extends GenericDtoMapper<TestSession, TestSessionDto> {

  /** ModelMapper. */
  @Autowired private ModelMapper modelMapper;

  /** UserDtoMapper. */
  @Autowired
  @Qualifier("applicationDtoMapper")
  private ApplicationDtoMapper applicationDtoMapper;

  @Autowired
  @Qualifier("projectResultDtoMapper")
  private ProjectResultDtoMapper projectResultDtoMapper;

  @Autowired
  @Qualifier("sessionDurationDtoMapper")
  private SessionDurationDtoMapper sessionDurationDtoMapper;

  @Autowired
  @Qualifier("versionDtoMapper")
  private VersionDtoMapper versionDtoMapper;

  @Autowired
  @Qualifier("rOperationUnexpectedDtoMapper")
  private ROperationUnexpectedDtoMapper rOperationUnexpectedDtoMapper;

  /**
   * Converts a {@link TestSession} into a {@link TestSessionListDto}.
   *
   * @param testSession, the {@link TestSession} to convert
   * @return a {@link TestSessionListDto}
   */
  public TestSessionListDto convertToSessionListDto(TestSession testSession) {
    final TestSessionListDto sessionListDto = new TestSessionListDto();
    sessionListDto.setId(testSession.getId());
    sessionListDto.setApplication(
        applicationDtoMapper.convertToApplicationListDto(testSession.getApplication()));
    sessionListDto.setCreationDate(testSession.getCreationDate());
    sessionListDto.setDescription(testSession.getDescription());
    sessionListDto.setSessionStatus(testSession.getSessionStatus());
    return sessionListDto;
  }

  /**
   * Converts a {@link TestSession} into a {@link TestSessionDto}.
   *
   * @param testSession, the {@link TestSession} to convert
   * @return a {@link TestSessionDto}
   */
  public TestSessionDto convertToSessionDto(TestSession testSession) {
    final TestSessionDto sessionDto = modelMapper.map(testSession, TestSessionDto.class);
    sessionDto.setProjectResults(new ArrayList<>());
    if (testSession.getProjectResults() != null) {
      testSession
          .getProjectResults()
          .forEach(
              project ->
                  sessionDto
                      .getProjectResults()
                      .add(projectResultDtoMapper.convertToProjectResultDto(project)));
    }
    if (testSession.getSessionDuration() != null) {
      sessionDto.setSessionDuration(
          sessionDurationDtoMapper.convertToDto(testSession.getSessionDuration()));
    }
    if (testSession.getApplication() != null) {
      sessionDto.setApplication(
          applicationDtoMapper.convertToApplicationDto(testSession.getApplication()));
    }
    if (testSession.getVersion() != null) {
      sessionDto.setVersion(versionDtoMapper.convertToVersionDto(testSession.getVersion()));
    }
    return sessionDto;
  }

  /**
   * Converts a {@link TestSessionDto} into a {@link TestSession}.
   *
   * @param sessionDto, the {@link TestSessionDto} to convert
   * @return a {@link TestSession}
   */
  public TestSession convertToTestSession(TestSessionDto sessionDto) {
    final TestSession session = modelMapper.map(sessionDto, TestSession.class);
    session.setProjectResults(new ArrayList<>());
    if (sessionDto.getApplication() != null) {
      session.setApplication(
          applicationDtoMapper.convertToApplication(sessionDto.getApplication()));
    }
    if (sessionDto.getVersion() != null) {
      session.setVersion(versionDtoMapper.convertToVersion(sessionDto.getVersion()));
    }
    if (sessionDto.getSessionDuration() != null) {
      session.setSessionDuration(
          sessionDurationDtoMapper.convertToEntity(sessionDto.getSessionDuration()));
    }
    if (!sessionDto.getProjectResults().isEmpty()) {
      sessionDto
          .getProjectResults()
          .forEach(
              project ->
                  session
                      .getProjectResults()
                      .add(projectResultDtoMapper.convertToProjectResult(project)));
    }
    return session;
  }

  /**
   * Converts a {@link TestSession} into a {@link TestSessionDetailDto}.
   *
   * @param session, the {@link TestSession} to convert
   * @return a {@link TestSessionDetailDto}
   */
  public TestSessionDetailDto convertToTestSessionDetailDto(TestSession session) {
    final TestSessionDetailDto sessionDto = modelMapper.map(session, TestSessionDetailDto.class);
    sessionDto.setProjectResults(new ArrayList<>());
    if (!session.getProjectResults().isEmpty()) {
      session
          .getProjectResults()
          .forEach(
              project ->
                  sessionDto
                      .getProjectResults()
                      .add(projectResultDtoMapper.convertToProjectResultDetailDto(project)));
    }
    if (session.getSessionDuration() != null) {
      sessionDto.setSessionDuration(
          sessionDurationDtoMapper.convertToDto(session.getSessionDuration()));
    }

    if (!session.getSessionResultOperations().isEmpty()) {
      sessionDto.setrOperationDto(new ArrayList<>());
      session
          .getSessionResultOperations()
          .forEach(
              operation ->
                  sessionDto
                      .getrOperationDto()
                      .add(rOperationUnexpectedDtoMapper.convertToDto(operation)));
    }
    return sessionDto;
  }

  /**
   * Converts a {@link TestSession} into a {@link TestSessionGestionDto}.
   *
   * @param session, the {@link TestSession} to convert
   * @return a {@link TestSessionGestionDto}
   */
  public TestSessionGestionDto convertToSessionGestionDto(TestSession session) {
    final TestSessionGestionDto sessionDto = modelMapper.map(session, TestSessionGestionDto.class);
    sessionDto.setApplication(
        applicationDtoMapper.convertToApplicationListDto(session.getApplication()));
    return sessionDto;
  }
}
