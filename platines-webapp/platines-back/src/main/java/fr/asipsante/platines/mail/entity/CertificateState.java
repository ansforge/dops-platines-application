/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.mail.entity;

import java.util.Date;

/**
 * @author aboittiaux
 */
public class CertificateState {

  /** Validity date. */
  private Date validityDate;

  /** Error message. */
  private String msgError;

  /** Constructeur. */
  public CertificateState() {
    super();
  }

  /**
   * @return the validityDate
   */
  public Date getValidityDate() {
    return validityDate;
  }

  /**
   * @param validityDate the validityDate to set
   */
  public void setValidityDate(Date validityDate) {
    this.validityDate = validityDate;
  }

  /**
   * @return the msgError
   */
  public String getMsgError() {
    return msgError;
  }

  /**
   * @param msgError the msgError to set
   */
  public void setMsgError(String msgError) {
    this.msgError = msgError;
  }
}
