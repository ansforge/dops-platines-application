/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.entity;

import fr.asipsante.platines.entity.enums.SessionStatus;
import fr.asipsante.platines.entity.enums.SessionType;
import fr.asipsante.platines.entity.listeners.SessionListener;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.Sid;

/**
 * Class TestSession corresponding to the table "session_tst" in the database.
 *
 * @author apierre
 */
@Entity
@Table(name = "session_tst")
@EntityListeners(SessionListener.class)
public class TestSession extends AbstractEntity implements Serializable {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 0L;

  /** The session date. */
  @Column(name = "date")
  private Date creationDate;

  /** The session execution date. */
  @Column(name = "dateExecution")
  private Date executionDate;

  /** The session execution duration. */
  @Column(name = "dureeExecution")
  private Long executionDuration;

  /** The session description. */
  @Column(name = "description", columnDefinition = "TEXT")
  private String description;

  /** The application to test. */
  @ManyToOne
  @JoinColumn(name = "APPLICATION_ID", nullable = false)
  private Application application;

  /** The session status. */
  @Enumerated(EnumType.STRING)
  @Column(name = "statutSession", length = 25)
  private SessionStatus sessionStatus;

  /** The session type. */
  @Enumerated(EnumType.STRING)
  @Column(name = "typeSession", length = 25)
  private SessionType sessionType;

  /** The session log. */
  @Lob
  @Basic(fetch = FetchType.LAZY)
  @Column(name = "logSession", columnDefinition = "longblob")
  private byte[] log;

  /** The projects result. */
  @OneToMany(mappedBy = "testSession", cascade = CascadeType.REMOVE, orphanRemoval = true)
  @OrderBy("numOrdre ASC")
  private List<ProjectResult> projectResults;

  /** The project version. */
  @ManyToOne
  @JoinColumn(name = "VERSION_ID", nullable = false)
  private Version version;

  /** The session duration. */
  @ManyToOne()
  @JoinColumn(name = "DUREESESSION_ID", nullable = true)
  private SessionDuration sessionDuration;

  /** The session UUID. */
  @Column(name = "uuidSession")
  private String uuid;

  /** The session suppression date. */
  @Column(name = "dateSupr")
  private Date suppressionDate;

  /** The session operations. */
  @OneToMany(mappedBy = "testSession", cascade = CascadeType.REMOVE, orphanRemoval = true)
  private List<ROperationUnexpected> sessionResultOperations;

  /**
   * Gets the session creation date.
   *
   * @return the date
   */
  public Date getCreationDate() {
    return creationDate;
  }

  /**
   * Sets the creation date.
   *
   * @param creationDate, the new date to set
   */
  public void setCreationDate(Date creationDate) {
    this.creationDate = creationDate;
  }

  /**
   * Gets the application to test.
   *
   * @return the application
   */
  public Application getApplication() {
    return application;
  }

  /**
   * Sets the application to test.
   *
   * @param application, the new application to set
   */
  public void setApplication(Application application) {
    this.application = application;
  }

  /**
   * Gets the session status.
   *
   * @return the session status
   */
  public SessionStatus getSessionStatus() {
    return sessionStatus;
  }

  /**
   * Sets the session status.
   *
   * @param sessionStatus, the new status to set
   */
  public void setSessionStatus(SessionStatus sessionStatus) {
    this.sessionStatus = sessionStatus;
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
  public List<ProjectResult> getProjectResults() {
    return projectResults;
  }

  /**
   * Sets the project results.
   *
   * @param projectResults, the project results to set
   */
  public void setProjectResults(List<ProjectResult> projectResults) {
    this.projectResults = projectResults;
  }

  /**
   * Gets the session version.
   *
   * @return the version
   */
  public Version getVersion() {
    return version;
  }

  /**
   * Sets the session version.
   *
   * @param version, the new version to set
   */
  public void setVersion(Version version) {
    this.version = version;
  }

  /**
   * Gets the execution date.
   *
   * @return the execution date
   */
  public Date getExecutionDate() {
    return executionDate;
  }

  /**
   * Sets the execution date.
   *
   * @param executionDate the new date execution to set
   */
  public void setExecutionDate(Date executionDate) {
    this.executionDate = executionDate;
  }

  /**
   * Gets the execution duration.
   *
   * @return the execution duration
   */
  public Long getExecutionDuration() {
    return executionDuration;
  }

  /**
   * Sets the execution duration.
   *
   * @param executionDuration, the new execution duration to set
   */
  public void setExecutionDuration(Long executionDuration) {
    this.executionDuration = executionDuration;
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
  public SessionDuration getSessionDuration() {
    return sessionDuration;
  }

  /**
   * Sets the session duration.
   *
   * @param sessionDuration, the new duration to set
   */
  public void setSessionDuration(SessionDuration sessionDuration) {
    this.sessionDuration = sessionDuration;
  }

  /**
   * Gets the session log.
   *
   * @return the log
   */
  public byte[] getLog() {
    return log;
  }

  /**
   * Sets the session logs.
   *
   * @param log, the new log to set
   */
  public void setLog(byte[] log) {
    this.log = log;
  }

  /**
   * Gets the session uuid.
   *
   * @return the session uuid
   */
  public String getUuid() {
    return uuid;
  }

  /**
   * Sets the session uuid.
   *
   * @param uuid, the new uuid to set
   */
  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  /**
   * gets the suppression date.
   *
   * @return the suppressionDate
   */
  public Date getSuppressionDate() {
    return suppressionDate;
  }

  /**
   * Sets the suppression date.
   *
   * @param suppressionDate, the new suppression date to set
   */
  public void setSuppressionDate(Date suppressionDate) {
    this.suppressionDate = suppressionDate;
  }

  /**
   * @return the sessionResultOperations
   */
  public List<ROperationUnexpected> getSessionResultOperations() {
    return sessionResultOperations;
  }

  /**
   * @param sessionResultOperations the sessionResultOperations to set
   */
  public void setSessionResultOperations(List<ROperationUnexpected> sessionResultOperations) {
    this.sessionResultOperations = sessionResultOperations;
  }

  @Override
  public Sid getOwner() {
    return new PrincipalSid(getApplication().getUser().getMail());
  }
}
