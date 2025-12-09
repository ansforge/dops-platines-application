/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.entity;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

/**
 * @author aboittiaux
 */
@Entity
@Table(name = "r_operation_expected")
@PrimaryKeyJoinColumn(name = "id")
public class ROperationExpected extends RServerOperationHistory {

  /** uuid. */
  private static final long serialVersionUID = -3725577377572946587L;

  /** test case. */
  @ManyToOne
  @JoinColumn(name = "RCASE_ID")
  private TestCaseResult testCaseResult;

  /**
   * @return the testCaseResult
   */
  public TestCaseResult getTestCaseResult() {
    return testCaseResult;
  }

  /**
   * @param testCaseResult the testCaseResult to set
   */
  public void setTestCaseResult(TestCaseResult testCaseResult) {
    this.testCaseResult = testCaseResult;
  }
}
