/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.dto;

import fr.asipsante.platines.entity.enums.ResultStatus;
import java.util.List;

/**
 * The type Project result dto.
 *
 * @author apierre
 */
public class ProjectResultDto {

  /** The project id. */
  private Long id;

  /** The tested project id. */
  private Long idProject;

  /** The project name. */
  private String name;

  /** The execution order. */
  private int numberOrder;

  /** The project properties. */
  private List<ProjectResultPropertyDto> projectProperties;

  /** The project status. */
  private ResultStatus resultStatus;

  /**
   * Gets the project id.
   *
   * @return the id
   */
  public Long getId() {
    return id;
  }

  /**
   * Sets the project id.
   *
   * @param id the id
   */
  public void setId(Long id) {
    this.id = id;
  }

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
   * Gets the project properties.
   *
   * @return the project properties
   */
  public List<ProjectResultPropertyDto> getProjectProperties() {
    return projectProperties;
  }

  /**
   * Sets the project properties.
   *
   * @param projectPoperties the project poperties
   */
  public void setProjectProperties(List<ProjectResultPropertyDto> projectPoperties) {
    this.projectProperties = projectPoperties;
  }

  /**
   * Gets result status.
   *
   * @return the resultStatus
   */
  public ResultStatus getResultStatus() {
    return resultStatus;
  }

  /**
   * Sets result status.
   *
   * @param resultStatus the resultStatus to set
   */
  public void setResultStatus(ResultStatus resultStatus) {
    this.resultStatus = resultStatus;
  }
}
