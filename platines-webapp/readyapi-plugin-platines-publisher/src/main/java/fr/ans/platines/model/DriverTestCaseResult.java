/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.ans.platines.model;

import java.util.ArrayList;
import java.util.List;

/**
 * The Class SoapUIDriverTestCaseResult.
 *
 * @author adml_zhsin
 */
public class DriverTestCaseResult extends DriverResult {

  /** The step results. */
  List<DriverTestStepResult> stepResults = new ArrayList<>();

  /** The Test Case Criticality */
  String criticality;

  public String getCriticality() {
    return criticality;
  }

  public void setCriticality(String criticality) {
    this.criticality = criticality;
  }

  /**
   * Gets the step results.
   *
   * @return the step results
   */
  public List<DriverTestStepResult> getStepResults() {
    return stepResults;
  }

  /**
   * Sets the step results.
   *
   * @param stepResults the new step results
   */
  public void setStepResults(List<DriverTestStepResult> stepResults) {
    this.stepResults = stepResults;
  }
}
