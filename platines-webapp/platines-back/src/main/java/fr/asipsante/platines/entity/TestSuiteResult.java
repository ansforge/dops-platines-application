/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.entity;

import fr.asipsante.platines.entity.enums.ResultStatus;
import fr.asipsante.platines.entity.listeners.SessionListener;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.Sid;

/**
 * Class TestSuiteResult corresponding to the table "r_suite" in the database.
 *
 * @author apierre
 */
@Entity
@Table(name = "r_suite")
@EntityListeners(SessionListener.class)
public class TestSuiteResult extends AbstractEntity implements Serializable {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 0L;

  /** The suite name. */
  @Column(name = "nom", length = 50)
  private String name;

  /** The suite description. */
  @Column(name = "description", columnDefinition = "TEXT")
  private String description;

  /** The suite execution date. */
  @Column(name = "dateExecution")
  private Date executionDate;

  /** The execution duration. */
  @Column(name = "dureeExecution")
  private Long executionDuration;

  /** The errors. */
  @Column(name = "erreurs", columnDefinition = "LONGTEXT")
  private String errors;

  /** The result status. */
  @Enumerated(EnumType.STRING)
  @Column(name = "statutResultat", length = 25)
  private ResultStatus resultStatus;

  /** The test cases associate. */
  @OneToMany(mappedBy = "testSuite", cascade = CascadeType.ALL)
  private List<TestCaseResult> testCases;

  /** the project result. */
  @ManyToOne
  @JoinColumn(name = "RPROJET_ID")
  private ProjectResult projectResult;

  /**
   * Gets the suite name.
   *
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the name.
   *
   * @param name the name
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Gets the suite description.
   *
   * @return the description
   */
  public String getDescription() {
    return description;
  }

  /**
   * Sets the suite description.
   *
   * @param description the description
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Gets the tests cases.
   *
   * @return the test cases
   */
  public List<TestCaseResult> getTestCases() {
    return testCases;
  }

  /**
   * Sets the test cases.
   *
   * @param testCases the test cases
   */
  public void setTestCases(List<TestCaseResult> testCases) {
    this.testCases = testCases;
  }

  /**
   * Gets the suite execution date.
   *
   * @return the execution date
   */
  public Date getExecutionDate() {
    return executionDate;
  }

  /**
   * Sets the execution date.
   *
   * @param executionDate the execution date
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
   * @param executionDuration the new execution duration to set
   */
  public void setExecutionDuration(Long executionDuration) {
    this.executionDuration = executionDuration;
  }

  /**
   * Gets the result status.
   *
   * @return the result status
   */
  public ResultStatus getResultStatus() {
    return resultStatus;
  }

  /**
   * Sets the result status.
   *
   * @param resultStatus the result status
   */
  public void setResultStatus(ResultStatus resultStatus) {
    this.resultStatus = resultStatus;
  }

  /**
   * Gets the suite errors.
   *
   * @return the errors
   */
  public String getErrors() {
    return errors;
  }

  /**
   * Sets the suite errors.
   *
   * @param errors the errors
   */
  public void setErrors(String errors) {
    this.errors = errors;
  }

  /**
   * Gets project result.
   *
   * @return the projectResult
   */
  public ProjectResult getProjectResult() {
    return projectResult;
  }

  /**
   * Sets project result.
   *
   * @param projectResult the projectResult to set
   */
  public void setProjectResult(ProjectResult projectResult) {
    this.projectResult = projectResult;
  }

  @Override
  public Sid getOwner() {
    return new PrincipalSid(projectResult.getTestSession().getApplication().getUser().getMail());
  }
}
