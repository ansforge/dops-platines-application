/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.dto;

import fr.asipsante.platines.entity.enums.SessionStatus;
import java.util.Date;

/**
 * @author apierre
 */
public class TestSessionGestionDto {

  /** The session id. */
  private Long id;

  /** The session date. */
  private Date executionDate;

  /** The application to test. */
  private ApplicationListDto application;

  /** The session status. */
  private SessionStatus sessionStatus;

  private Date creationDate;

  private String description;

  /**
   * @return the id
   */
  public Long getId() {
    return id;
  }

  /**
   * @param id the id to set
   */
  public void setId(Long id) {
    this.id = id;
  }

  /**
   * @return the executionDate
   */
  public Date getExecutionDate() {
    return executionDate;
  }

  /**
   * @param executionDate the executionDate to set
   */
  public void setExecutionDate(Date executionDate) {
    this.executionDate = executionDate;
  }

  /**
   * @return the application
   */
  public ApplicationListDto getApplication() {
    return application;
  }

  /**
   * @param application the application to set
   */
  public void setApplication(ApplicationListDto application) {
    this.application = application;
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
   * @return the description
   */
  public String getDescription() {
    return description;
  }

  /**
   * @param description the description to set
   */
  public void setDescription(String description) {
    this.description = description;
  }
}
