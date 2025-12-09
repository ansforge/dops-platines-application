/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.dto;

import java.util.Date;

/**
 * @author aboittiaux
 */
public class RServerOperationHistoryDto {

  /** operation date. */
  private Date operationDate;

  /** time taken. */
  private Long timeTaken = 0L;

  /** operation. */
  private String operation;

  /** response name. */
  private String responseName;

  /**
   * @return the operationDate
   */
  public Date getOperationDate() {
    if (operationDate != null) {
      return operationDate;
    } else {
      return new Date(0L);
    }
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
}
