/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.ans.platines.model;

import java.util.ArrayList;
import java.util.List;

/**
 * The Class SoapUIDriverTestSuiteResult.
 *
 * @author adml_zhsin
 */
public class DriverTestSuiteResult extends DriverResult {

  /** The case results. */
  List<DriverTestCaseResult> caseResults = new ArrayList<>();

  /**
   * Gets the case results.
   *
   * @return the case results
   */
  public List<DriverTestCaseResult> getCaseResults() {
    return caseResults;
  }

  /**
   * Sets the case results.
   *
   * @param caseResults the new case results
   */
  public void setCaseResults(List<DriverTestCaseResult> caseResults) {
    this.caseResults = caseResults;
  }
}
