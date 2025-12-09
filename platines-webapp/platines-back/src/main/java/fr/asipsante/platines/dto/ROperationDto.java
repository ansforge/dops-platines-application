/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.dto;

import java.util.Date;

/**
 * @author aboittiaux
 */
public class ROperationDto {

  /** operation id. */
  private Long id;

  /** operation date. */
  private Date operationDate;

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
}
