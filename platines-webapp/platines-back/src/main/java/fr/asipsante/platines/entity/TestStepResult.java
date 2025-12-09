/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.entity;

import fr.asipsante.platines.entity.enums.ResultStatus;
import fr.asipsante.platines.entity.listeners.SessionListener;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.Sid;

/**
 * Class TestStepResult corresponding to the table "r_step" in the database.
 *
 * @author apierre
 */
@Entity
@Table(name = "r_step")
@EntityListeners(SessionListener.class)
public class TestStepResult extends AbstractEntity implements Serializable {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 0L;

  /** The step name. */
  @Column(name = "nom", length = 50)
  private String name;

  /** The step description. */
  @Column(name = "description", columnDefinition = "TEXT")
  private String description;

  /** The step execution date. */
  @Column(name = "dateExecution")
  private Date executionDate;

  /** The step duration of execution. */
  @Column(name = "dureeExecution")
  private Long executionDuration;

  /** The step errors. */
  @Column(name = "erreurs", columnDefinition = "LONGTEXT")
  private String error;

  /** The result status of the step. */
  @Enumerated(EnumType.STRING)
  @Column(name = "statutResultat", length = 25)
  private ResultStatus status;

  /** The request. */
  @Column(name = "request")
  private String request;

  /** The response. */
  @Column(name = "response")
  private String response;

  /** the tests case. */
  @ManyToOne
  @JoinColumn(name = "RCASE_ID")
  private TestCaseResult testCase;

  /**
   * Gets the step name.
   *
   * @return the step name
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the name.
   *
   * @param name, the new name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Gets the step description.
   *
   * @return the step description
   */
  public String getDescription() {
    return description;
  }

  /**
   * Sets the step description.
   *
   * @param description, the new description to set
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Gets the result status.
   *
   * @return the result status
   */
  public ResultStatus getStatus() {
    return status;
  }

  /**
   * Sets the result status.
   *
   * @param status, the new status to set
   */
  public void setStatus(ResultStatus status) {
    this.status = status;
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
   * @param executionDate, the new execution date to set
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
   * Gets the step errors.
   *
   * @return step errors.
   */
  public String getError() {
    return error;
  }

  /**
   * Sets the step errors.
   *
   * @param error, the new error to set
   */
  public void setError(String error) {
    this.error = error;
  }

  /**
   * @return the request
   */
  public String getRequest() {
    return request;
  }

  /**
   * @param request the request to set
   */
  public void setRequest(String request) {
    this.request = request;
  }

  /**
   * @return the response
   */
  public String getResponse() {
    return response;
  }

  /**
   * @param response the response to set
   */
  public void setResponse(String response) {
    this.response = response;
  }

  /**
   * @return the testCase
   */
  public TestCaseResult getTestCase() {
    return testCase;
  }

  /**
   * @param testCase the testCase to set
   */
  public void setTestCase(TestCaseResult testCase) {
    this.testCase = testCase;
  }

  @Override
  public Sid getOwner() {
    return new PrincipalSid(
        getTestCase()
            .getTestSuite()
            .getProjectResult()
            .getTestSession()
            .getApplication()
            .getUser()
            .getMail());
  }
}
