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
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.Sid;

/**
 * Class ProjectResult corresponding to the table "r_projet" in the database.
 *
 * @author apierre
 */
@Entity
@Table(name = "r_projet")
@EntityListeners(SessionListener.class)
public class ProjectResult extends AbstractEntity implements Serializable {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 0L;

  /** The tested project id. */
  @Column(name = "idProject")
  private Long idProject;

  /** The project name. */
  @Column(name = "nom", length = 50)
  private String name;

  /** The execution order. */
  @Column(name = "numOrdre")
  private int numberOrder;

  /** The project description. */
  @Column(name = "description", columnDefinition = "TEXT")
  private String description;

  /** The execution date. */
  @Column(name = "dateExecution")
  private Date executionDate;

  /** The execution duration. */
  @Column(name = "dureeExecution")
  private Long executionDuration;

  /** The result status. */
  @Enumerated(EnumType.STRING)
  @Column(name = "statutResultat", length = 25)
  private ResultStatus resultStatus;

  /** The test suites. */
  @OneToMany(mappedBy = "projectResult", cascade = CascadeType.ALL)
  private List<TestSuiteResult> testSuites;

  /** The project properties. */
  @OneToMany(
      fetch = FetchType.LAZY,
      mappedBy = "projectResult",
      cascade = CascadeType.REMOVE,
      orphanRemoval = true)
  private List<ProjectResultProperty> projectProperties;

  /** The project error. */
  @Column(name = "erreurs", columnDefinition = "LONGTEXT")
  private String error;

  /** the session. */
  @ManyToOne
  @JoinColumn(name = "SESSIONTST_ID", nullable = false)
  private TestSession testSession;

  /**
   * Gets the tested project id.
   *
   * @return the tested project id.
   */
  public Long getIdProject() {
    return idProject;
  }

  /**
   * Sets the tested project id.
   *
   * @param idProject the id project
   */
  public void setIdProject(Long idProject) {
    this.idProject = idProject;
  }

  /**
   * Gets the project name.
   *
   * @return the project name
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the project name.
   *
   * @param name the name
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Gets the number order.
   *
   * @return the number order
   */
  public int getNumberOrder() {
    return numberOrder;
  }

  /**
   * Sets the number order.
   *
   * @param numberOrder the number order
   */
  public void setNumberOrder(int numberOrder) {
    this.numberOrder = numberOrder;
  }

  /**
   * Gets the project description.
   *
   * @return the description
   */
  public String getDescription() {
    return description;
  }

  /**
   * Sets the project description.
   *
   * @param description the description
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Gets the test suites.
   *
   * @return the test suites
   */
  public List<TestSuiteResult> getTestSuites() {
    return testSuites;
  }

  /**
   * Sets the test suites.
   *
   * @param testSuites the test suites
   */
  public void setTestSuites(List<TestSuiteResult> testSuites) {
    this.testSuites = testSuites;
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
   * Gets the project error.
   *
   * @return the error
   */
  public String getError() {
    return error;
  }

  /**
   * Sets the project error.
   *
   * @param error the error
   */
  public void setError(String error) {
    this.error = error;
  }

  /**
   * Gets the project properties.
   *
   * @return the project properties
   */
  public List<ProjectResultProperty> getProjectProperties() {
    return projectProperties;
  }

  /**
   * Sets the project properties.
   *
   * @param projectPoperties the project poperties
   */
  public void setProjectProperties(List<ProjectResultProperty> projectPoperties) {
    this.projectProperties = projectPoperties;
  }

  /**
   * Gets test session.
   *
   * @return the testSession
   */
  public TestSession getTestSession() {
    return testSession;
  }

  /**
   * Sets test session.
   *
   * @param testSession the testSession to set
   */
  public void setTestSession(TestSession testSession) {
    this.testSession = testSession;
  }

  @Override
  public Sid getOwner() {
    return new PrincipalSid(getTestSession().getApplication().getUser().getMail());
  }
}
