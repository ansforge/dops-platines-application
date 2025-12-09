/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.dto;

import fr.asipsante.platines.entity.enums.ResultStatus;
import java.util.List;

/**
 * @author apierre
 */
public class ProjectResultDetailDto {

  /** The project id. */
  private Long id;

  /** The project name. */
  private String name;

  /** The project description. */
  private String description;

  /** The result status. */
  private ResultStatus resultStatus;

  /** The test suites. */
  private List<TestSuiteDto> testSuites;

  /** The project properties. */
  private List<ProjectResultPropertyDto> projectProperties;

  /** the project id. */
  private Long idProject;

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
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * @param name the name to set
   */
  public void setName(String name) {
    this.name = name;
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

  /**
   * @return the resultStatus
   */
  public ResultStatus getResultStatus() {
    return resultStatus;
  }

  /**
   * @param resultStatus the resultStatus to set
   */
  public void setResultStatus(ResultStatus resultStatus) {
    this.resultStatus = resultStatus;
  }

  /**
   * @return the testSuites
   */
  public List<TestSuiteDto> getTestSuites() {
    return testSuites;
  }

  /**
   * @param testSuites the testSuites to set
   */
  public void setTestSuites(List<TestSuiteDto> testSuites) {
    this.testSuites = testSuites;
  }

  /**
   * @return the projectProperties
   */
  public List<ProjectResultPropertyDto> getProjectProperties() {
    return projectProperties;
  }

  /**
   * @param projectProperties the projectProperties to set
   */
  public void setProjectProperties(List<ProjectResultPropertyDto> projectProperties) {
    this.projectProperties = projectProperties;
  }

  /**
   * @return the idProject
   */
  public Long getIdProject() {
    return idProject;
  }

  /**
   * @param idProject the idProject to set
   */
  public void setIdProject(Long idProject) {
    this.idProject = idProject;
  }
}
