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
 * Class TestCaseResut corresponding to the table "r_case" in the database.
 *
 * @author apierre
 */
@Entity
@Table(name = "r_case")
@EntityListeners(SessionListener.class)
public class TestCaseResult extends AbstractEntity implements Serializable {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 0L;

  /** The case name. */
  @Column(name = "nom", length = 50)
  private String name;

  /** The case criticality. */
  @Column(name = "criticite", length = 50)
  private String criticality;

  /** The case description. */
  @Column(name = "description", columnDefinition = "TEXT")
  private String description;

  /** The case execution date. */
  @Column(name = "dateExecution")
  private Date executionDate;

  /** The case duration. */
  @Column(name = "dureeExecution")
  private Long executionDuration;

  /** The errors. */
  @Column(name = "erreurs", columnDefinition = "LONGTEXT")
  private String errors;

  /** The result status. */
  @Enumerated(EnumType.STRING)
  @Column(name = "statutResultat", length = 25)
  private ResultStatus resultStatus;

  /** The test steps. */
  @OneToMany(mappedBy = "testCase", cascade = CascadeType.ALL)
  private List<TestStepResult> testSteps;

  /** the test suite. */
  @ManyToOne
  @JoinColumn(name = "RSUITE_ID")
  private TestSuiteResult testSuite;

  /** the test case operations. */
  @OneToMany(mappedBy = "testCaseResult", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ROperationExpected> caseResultOperations;

  /** the response name. */
  @Column(name = "response_name")
  private String responseName;

  /**
   * Gets the case name.
   *
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the case name.
   *
   * @param name the name
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Gets the case Criticality.
   *
   * @return the Criticality
   */
  public String getCriticality() {
    return criticality;
  }

  /**
   * Sets the case Criticality.
   *
   * @param criticality the criticality
   */
  public void setCriticality(String criticality) {
    this.criticality = criticality;
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
   * @param executionDuration the execution duration
   */
  public void setExecutionDuration(Long executionDuration) {
    this.executionDuration = executionDuration;
  }

  /**
   * Gets the case description.
   *
   * @return the description
   */
  public String getDescription() {
    return description;
  }

  /**
   * Sets the case description.
   *
   * @param description the description
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Gets the test steps results.
   *
   * @return the testStepsResults
   */
  public List<TestStepResult> getTestSteps() {
    return testSteps;
  }

  /**
   * Sets the test steps results.
   *
   * @param testSteps the test steps
   */
  public void setTestSteps(List<TestStepResult> testSteps) {
    this.testSteps = testSteps;
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
   * Gets case errors.
   *
   * @return error errors
   */
  public String getErrors() {
    return errors;
  }

  /**
   * Sets case errors.
   *
   * @param errors the errors
   */
  public void setErrors(String errors) {
    this.errors = errors;
  }

  /**
   * Gets test suite.
   *
   * @return the testSuite
   */
  public TestSuiteResult getTestSuite() {
    return testSuite;
  }

  /**
   * Sets test suite.
   *
   * @param testSuite the testSuite to set
   */
  public void setTestSuite(TestSuiteResult testSuite) {
    this.testSuite = testSuite;
  }

  /**
   * Gets case result operations.
   *
   * @return the caseResultOperations
   */
  public List<ROperationExpected> getCaseResultOperations() {
    return caseResultOperations;
  }

  /**
   * Sets case result operations.
   *
   * @param caseResultOperations the caseResultOperations to set
   */
  public void setCaseResultOperations(List<ROperationExpected> caseResultOperations) {
    this.caseResultOperations = caseResultOperations;
  }

  /**
   * Gets response name.
   *
   * @return the responseName
   */
  public String getResponseName() {
    return responseName;
  }

  /**
   * Sets response name.
   *
   * @param responseName the responseName to set
   */
  public void setResponseName(String responseName) {
    this.responseName = responseName;
  }

  @Override
  public Sid getOwner() {
    return new PrincipalSid(
        getTestSuite().getProjectResult().getTestSession().getApplication().getUser().getMail());
  }
}
