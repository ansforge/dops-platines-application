/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.dto;

import fr.asipsante.platines.entity.enums.SessionStatus;
import java.util.Date;
import java.util.List;

/**
 * The type Test session detail dto.
 *
 * @author apierre
 */
public class TestSessionDetailDto {

  /** The session id. */
  private Long id;

  /** The session execution duration. */
  private Long executionDuration;

  /** The session description. */
  private String description;

  /** The session status. */
  private SessionStatus sessionStatus;

  /** The projects result. */
  private List<ProjectResultDetailDto> projectResults;

  /** The session uuid. */
  private String uuid;

  /** The project execution date. */
  private Date executionDate;

  /** The session duration. */
  private SessionDurationDto sessionDuration;

  /** The mockservice url. */
  private String url;

  /** The mockservice resource path. */
  private List<String> resourcePath;

  /** The Result operation list. */
  private List<ROperationDto> rOperationDto;

  /** The project creationDate. */
  private Date creationDate;

  /**
   * Gets id.
   *
   * @return the id
   */
  public Long getId() {
    return id;
  }

  /**
   * Sets id.
   *
   * @param id the id to set
   */
  public void setId(Long id) {
    this.id = id;
  }

  /**
   * Gets execution duration.
   *
   * @return the executionDuration
   */
  public Long getExecutionDuration() {
    return executionDuration;
  }

  /**
   * Sets execution duration.
   *
   * @param executionDuration the executionDuration to set
   */
  public void setExecutionDuration(Long executionDuration) {
    this.executionDuration = executionDuration;
  }

  /**
   * Gets description.
   *
   * @return the description
   */
  public String getDescription() {
    return description;
  }

  /**
   * Sets description.
   *
   * @param description the description to set
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Gets session status.
   *
   * @return the sessionStatus
   */
  public SessionStatus getSessionStatus() {
    return sessionStatus;
  }

  /**
   * Sets session status.
   *
   * @param sessionStatus the sessionStatus to set
   */
  public void setSessionStatus(SessionStatus sessionStatus) {
    this.sessionStatus = sessionStatus;
  }

  /**
   * Gets project results.
   *
   * @return the projectResults
   */
  public List<ProjectResultDetailDto> getProjectResults() {
    return projectResults;
  }

  /**
   * Sets project results.
   *
   * @param projectResults the projectResults to set
   */
  public void setProjectResults(List<ProjectResultDetailDto> projectResults) {
    this.projectResults = projectResults;
  }

  /**
   * Gets execution date.
   *
   * @return the executionDate
   */
  public Date getExecutionDate() {
    return executionDate;
  }

  /**
   * Sets execution date.
   *
   * @param executionDate the executionDate to set
   */
  public void setExecutionDate(Date executionDate) {
    this.executionDate = executionDate;
  }

  /**
   * Gets uuid.
   *
   * @return the uuid
   */
  public String getUuid() {
    return uuid;
  }

  /**
   * Sets uuid.
   *
   * @param uuid the uuid to set
   */
  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  /**
   * Gets session duration.
   *
   * @return the sessionDuration
   */
  public SessionDurationDto getSessionDuration() {
    return sessionDuration;
  }

  /**
   * Sets session duration.
   *
   * @param sessionDuration the sessionDuration to set
   */
  public void setSessionDuration(SessionDurationDto sessionDuration) {
    this.sessionDuration = sessionDuration;
  }

  /**
   * Gets url.
   *
   * @return the url
   */
  public String getUrl() {
    return url;
  }

  /**
   * Sets url.
   *
   * @param url the url to set
   */
  public void setUrl(String url) {
    this.url = url;
  }

  /**
   * Gets resource path.
   *
   * @return the resource path
   */
  public List<String> getResourcePath() {
    return resourcePath;
  }

  /**
   * Sets resource path.
   *
   * @param resourcePath the resource path
   */
  public void setResourcePath(List<String> resourcePath) {
    this.resourcePath = resourcePath;
  }

  /**
   * Gets operation dto.
   *
   * @return the rOperationDto
   */
  public List<ROperationDto> getrOperationDto() {
    return rOperationDto;
  }

  /**
   * Sets operation dto.
   *
   * @param rOperationDto the rOperationDto to set
   */
  public void setrOperationDto(List<ROperationDto> rOperationDto) {
    this.rOperationDto = rOperationDto;
  }

  /**
   * Gets creation date.
   *
   * @return the creationDate
   */
  public Date getCreationDate() {
    return creationDate;
  }

  /**
   * Sets creation date.
   *
   * @param creationDate the creationDate to set
   */
  public void setCreationDate(Date creationDate) {
    this.creationDate = creationDate;
  }
}
