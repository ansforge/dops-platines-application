/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.dto;

/**
 * @author aboittiaux
 */
public class PemTestCertificate {
  /** file name. */
  private String fileName;

  /** pem. */
  private String pem;

  /**
   * @return the fileName
   */
  public String getFileName() {
    return fileName;
  }

  /**
   * @param fileName the fileName to set
   */
  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  /**
   * @return the pem
   */
  public String getPem() {
    return pem;
  }

  /**
   * @param pem the pem to set
   */
  public void setPem(String pem) {
    this.pem = pem;
  }
}
