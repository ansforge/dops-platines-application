/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.dto;

import fr.asipsante.platines.entity.enums.ResultStatus;
import java.util.List;

/**
 * @author apierre
 */
public class TestCaseResultDto {

  /** The case id. */
  private Long id;

  /** The case name. */
  private String name;

  /** The case description. */
  private String description;

  /** The case criticality. */
  private String criticality;

  /** The result status. */
  private ResultStatus resultStatus;

  /** The test cases associate. */
  private List<TestStepResultDto> testSteps;

  /** the result operations. */
  private List<ROperationDto> rOperationDto;

  /**
   * @return the testSteps
   */
  public List<TestStepResultDto> getTestSteps() {
    return testSteps;
  }

  /**
   * @param testSteps the testSteps to set
   */
  public void setTestSteps(List<TestStepResultDto> testSteps) {
    this.testSteps = testSteps;
  }

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
   * @return the criticality
   */
  public String getCriticality() {
    return criticality;
  }

  /**
   * @param criticality the criticality to set
   */
  public void setCriticality(String criticality) {
    this.criticality = criticality;
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
   * @return the rOperationDto
   */
  public List<ROperationDto> getrOperationDto() {
    return rOperationDto;
  }

  /**
   * @param rOperationDto, the rOperationExpectedDto to set
   */
  public void setrOperationDto(List<ROperationDto> rOperationDto) {
    this.rOperationDto = rOperationDto;
  }
}
