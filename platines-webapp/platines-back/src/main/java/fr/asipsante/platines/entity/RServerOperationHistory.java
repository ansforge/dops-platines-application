/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

/**
 * @author aboittiaux
 */
@Entity
@Table(name = "r_server_operation_history")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class RServerOperationHistory implements Serializable {

  /** uuid. */
  private static final long serialVersionUID = 7212982874232553990L;

  /***
   * the id.
   */
  @Id @GeneratedValue private Long id;

  /** the request. */
  @Column(name = "request")
  private String request;

  /** the response. */
  @Column(name = "response")
  private String response;

  /** the response name. */
  @Column(name = "response_name")
  private String responseName;

  /** the operation date. */
  @Column(name = "operation_date")
  private Date operationDate;

  /** time taken. */
  @Column(name = "time_taken")
  private Long timeTaken;

  /** the operation. */
  @Column(name = "operation")
  private String operation;

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
