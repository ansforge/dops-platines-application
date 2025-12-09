/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.model;

/**
 * The Class SoapUIDriverTestStepResult.
 *
 * @author adml_zhsin
 */
public class DriverTestStepResult extends DriverResult {

  /** request. */
  private String request;

  /** response. */
  private String response;

  /**
   * @return the request
   */
  public String getRequest() {
    return request;
  }

  /**
   * @param request the request to set
   */
  public void setRequest(String request) {
    this.request = request;
  }

  /**
   * @return the response
   */
  public String getResponse() {
    return response;
  }

  /**
   * @param response the response to set
   */
  public void setResponse(String response) {
    this.response = response;
  }
}
