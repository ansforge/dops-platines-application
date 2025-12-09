/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.ans.platines.model;

import java.util.Date;
import java.util.List;

/** The Class SoapUIDriverResult. */
public class DriverResult {

  /** The name. */
  private String name;

  /** The statut. */
  private String status;

  /** The content. */
  private String message;

  private String description;

  /** The errors. */
  private List<String> errors;

  /** The date execution. */
  private Date dateExecution;

  /** The execution time. */
  private Long executionTime;

  /**
   * Gets the statut.
   *
   * @return the statut
   */
  public String getStatut() {
    return status;
  }

  /**
   * Sets the statut.
   *
   * @param statut the new statut
   */
  public void setStatut(String statut) {
    this.status = statut;
  }

  /**
   * Gets the content.
   *
   * @return the content
   */
  public String getContent() {
    return message;
  }

  /**
   * Sets the content.
   *
   * @param content the new content
   */
  public void setContent(String content) {
    this.message = content;
  }

  /**
   * Gets the date execution.
   *
   * @return the date execution
   */
  public Date getDateExecution() {
    return dateExecution;
  }

  /**
   * Sets the date execution.
   *
   * @param dateExecution the new date execution
   */
  public void setDateExecution(Date dateExecution) {
    this.dateExecution = dateExecution;
  }

  /**
   * Gets the execution time.
   *
   * @return the execution time
   */
  public Long getExecutionTime() {
    return executionTime;
  }

  /**
   * Sets the execution time.
   *
   * @param executionTime the new execution time
   */
  public void setExecutionTime(Long executionTime) {
    this.executionTime = executionTime;
  }

  /**
   * Gets the name.
   *
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the name.
   *
   * @param name the new name
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Gets the errors.
   *
   * @return the errors
   */
  public List<String> getErrors() {
    return errors;
  }

  /**
   * Sets the errors.
   *
   * @param errors the new errors
   */
  public void setErrors(List<String> errors) {
    this.errors = errors;
  }

  /**
   * @return the status
   */
  public String getStatus() {
    return status;
  }

  /**
   * @param status the status to set
   */
  public void setStatus(String status) {
    this.status = status;
  }

  /**
   * @return the message
   */
  public String getMessage() {
    return message;
  }

  /**
   * @param message the message to set
   */
  public void setMessage(String message) {
    this.message = message;
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
}
