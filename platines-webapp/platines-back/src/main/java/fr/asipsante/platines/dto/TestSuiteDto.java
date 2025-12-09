/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.dto;

import fr.asipsante.platines.entity.enums.ResultStatus;
import java.util.List;

/**
 * @author apierre
 */
public class TestSuiteDto {

  /** The suite id. */
  private Long id;

  /** The suite name. */
  private String name;

  /** The suite description. */
  private String description;

  /** The result status. */
  private ResultStatus resultStatus;

  /** The test cases associate. */
  private List<TestCaseResultDto> testCases;

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
   * @return the testCases
   */
  public List<TestCaseResultDto> getTestCases() {
    return testCases;
  }

  /**
   * @param testCases the testCases to set
   */
  public void setTestCases(List<TestCaseResultDto> testCases) {
    this.testCases = testCases;
  }
}
