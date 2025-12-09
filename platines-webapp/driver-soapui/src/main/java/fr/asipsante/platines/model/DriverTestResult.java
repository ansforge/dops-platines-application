/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.model;

import java.util.ArrayList;
import java.util.List;

/**
 * The Class SoapUIDriverProjectResult.
 *
 * @author adml_zhsin
 */
public class DriverTestResult extends DriverResult {

  private String projectId;

  /** The suite results. */
  List<DriverTestSuiteResult> suiteResults = new ArrayList<>();

  /**
   * Gets the suite results.
   *
   * @return the suite results
   */
  public List<DriverTestSuiteResult> getSuiteResults() {
    return suiteResults;
  }

  /**
   * Sets the suite results.
   *
   * @param suiteResults the new suite results
   */
  public void setSuiteResults(List<DriverTestSuiteResult> suiteResults) {
    this.suiteResults = suiteResults;
  }

  /**
   * @return the projectId
   */
  public String getProjectId() {
    return projectId;
  }

  /**
   * @param projectId the projectId to set
   */
  public void setProjectId(String projectId) {
    this.projectId = projectId;
  }
}
