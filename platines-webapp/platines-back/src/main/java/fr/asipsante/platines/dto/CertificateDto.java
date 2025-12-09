/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.dto;

import java.util.Date;

/**
 * @author apierre
 */
public class CertificateDto {

  /** Certificate id. */
  private Long id;

  /** Certificate pem. */
  private String pem;

  /** Certificate file name. */
  private String fileName;

  /** Certificate file. */
  private byte[] file;

  /** validity date. */
  private Date validityDate;

  /** chain of trust. */
  private ChainOfTrustDto chainOfTrust;

  /** Default constructor. */
  public CertificateDto() {
    super();
  }

  /**
   * Gets the certificate id.
   *
   * @return certificate id
   */
  public Long getId() {
    return id;
  }

  /**
   * Sets the certificate id.
   *
   * @param id, the new id to set
   */
  public void setId(Long id) {
    this.id = id;
  }

  /**
   * Gets the certificate pem.
   *
   * @return the certificate pem
   */
  public String getPem() {
    return pem;
  }

  /**
   * Sets the certificate pem.
   *
   * @param pem, the new pem to set
   */
  public void setPem(String pem) {
    this.pem = pem;
  }

  /**
   * Gets the certificate file name.
   *
   * @return the certificate file name
   */
  public String getFileName() {
    return fileName;
  }

  /**
   * Sets the certificate file name.
   *
   * @param fileName, the new certificate file name
   */
  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  /**
   * Gets the certificate file.
   *
   * @return the certificate file
   */
  public byte[] getFile() {
    return file;
  }

  /**
   * Sets the certificate file.
   *
   * @param file, the new certificate file to set.
   */
  public void setFile(byte[] file) {
    this.file = file;
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
   * @return the chainOfTrust
   */
  public ChainOfTrustDto getChainOfTrust() {
    return chainOfTrust;
  }

  /**
   * @param chainOfTrust the chainOfTrust to set
   */
  public void setChainOfTrust(ChainOfTrustDto chainOfTrust) {
    this.chainOfTrust = chainOfTrust;
  }
}
