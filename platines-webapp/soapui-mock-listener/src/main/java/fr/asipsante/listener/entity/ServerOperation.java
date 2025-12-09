/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.listener.entity;

import java.util.Date;

/**
 * @author aboittiaux
 */
public class ServerOperation {

  private String responseName;

  private String request;

  private String response;

  private Long timeTaken;

  private Date operationDate;

  private String uuidSession;

  private String operation;

  /**
   * @return the responseName
   */
  public String getResponseName() {
    return responseName;
  }

  /**
   * @param responseName the responseName to set
   */
  public void setResponseName(String responseName) {
    this.responseName = responseName;
  }

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

  /**
   * @return the timeTaken
   */
  public Long getTimeTaken() {
    return timeTaken;
  }

  /**
   * @param timeTaken the timeTaken to set
   */
  public void setTimeTaken(Long timeTaken) {
    this.timeTaken = timeTaken;
  }

  /**
   * @return the operationDate
   */
  public Date getOperationDate() {
    return operationDate;
  }

  /**
   * @param operationDate the operationDate to set
   */
  public void setOperationDate(Date operationDate) {
    this.operationDate = operationDate;
  }

  /**
   * @return the uuidSession
   */
  public String getUuidSession() {
    return uuidSession;
  }

  /**
   * @param uuidSession the uuidSession to set
   */
  public void setUuidSession(String uuidSession) {
    this.uuidSession = uuidSession;
  }

  /**
   * @return the operation
   */
  public String getOperation() {
    return operation;
  }

  /**
   * @param operation the operation to set
   */
  public void setOperation(String operation) {
    this.operation = operation;
  }
}
