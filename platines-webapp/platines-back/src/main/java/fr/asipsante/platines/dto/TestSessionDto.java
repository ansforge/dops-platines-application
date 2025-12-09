/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.dto;

import fr.asipsante.platines.entity.enums.SessionStatus;
import fr.asipsante.platines.entity.enums.SessionType;
import java.util.Date;
import java.util.List;

/**
 * @author apierre
 */
public class TestSessionDto {

  /** The session id. */
  private Long id;

  /** The session description. */
  private String description;

  /** The application to test. */
  private ApplicationDto application;

  /** The session type. */
  private SessionType sessionType;

  /** The projects result. */
  private List<ProjectResultDto> projectResults;

  /** The project version. */
  private VersionDto version;

  /** The session duration. */
  private SessionDurationDto sessionDuration;

  /** The uuid session. */
  private String uuid;

  /** The session creation date. */
  private Date creationDate;

  /** The session status. */
  private SessionStatus sessionStatus;

  /** The chain of trust. */
  private ChainOfTrustDto chainOfTrustDto;

  /**
   * Gets the session id.
   *
   * @return the id
   */
  public Long getId() {
    return id;
  }

  /**
   * Sets the session id.
   *
   * @param id, the new id to set
   */
  public void setId(Long id) {
    this.id = id;
  }

  /**
   * Gets the application to test.
   *
   * @return the application
   */
  public ApplicationDto getApplication() {
    return application;
  }

  /**
   * Sets the application to test.
   *
   * @param application, the new application to set
   */
  public void setApplication(ApplicationDto application) {
    this.application = application;
  }

  /**
   * Gets the session type.
   *
   * @return the session type
   */
  public SessionType getSessionType() {
    return sessionType;
  }

  /**
   * Sets the session type.
   *
   * @param sessionType, the new type to set
   */
  public void setSessionType(SessionType sessionType) {
    this.sessionType = sessionType;
  }

  /**
   * Gets the project result.
   *
   * @return the project result
   */
  public List<ProjectResultDto> getProjectResults() {
    return projectResults;
  }

  /**
   * Sets the project results.
   *
   * @param projectResults, the project results to set
   */
  public void setProjectResults(List<ProjectResultDto> projectResults) {
    this.projectResults = projectResults;
  }

  /**
   * Gets the session version.
   *
   * @return the version
   */
  public VersionDto getVersion() {
    return version;
  }

  /**
   * Sets the session version.
   *
   * @param version, the new version to set
   */
  public void setVersion(VersionDto version) {
    this.version = version;
  }

  /**
   * Gets the session description.
   *
   * @return the description
   */
  public String getDescription() {
    return description;
  }

  /**
   * Sets the session description.
   *
   * @param description, the new description to set
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Gets the session duration.
   *
   * @return the session duration
   */
  public SessionDurationDto getSessionDuration() {
    return sessionDuration;
  }

  /**
   * Sets the session duration.
   *
   * @param sessionDuration, the new duration to set
   */
  public void setSessionDuration(SessionDurationDto sessionDuration) {
    this.sessionDuration = sessionDuration;
  }

  /**
   * @return the uuid
   */
  public String getUuid() {
    return uuid;
  }

  /**
   * @param uuid the uuid to set
   */
  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  /**
   * @return the creationDate
   */
  public Date getCreationDate() {
    return creationDate;
  }

  /**
   * @param creationDate the creationDate to set
   */
  public void setCreationDate(Date creationDate) {
    this.creationDate = creationDate;
  }

  /**
   * @return the sessionStatus
   */
  public SessionStatus getSessionStatus() {
    return sessionStatus;
  }

  /**
   * @param sessionStatus the sessionStatus to set
   */
  public void setSessionStatus(SessionStatus sessionStatus) {
    this.sessionStatus = sessionStatus;
  }

  /**
   * @return the chainOfTrustDto
   */
  public ChainOfTrustDto getChainOfTrustDto() {
    return chainOfTrustDto;
  }

  /**
   * @param chainOfTrustDto the chainOfTrustDto to set
   */
  public void setChainOfTrustDto(ChainOfTrustDto chainOfTrustDto) {
    this.chainOfTrustDto = chainOfTrustDto;
  }
}
