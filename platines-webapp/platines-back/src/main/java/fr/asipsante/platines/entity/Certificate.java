/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.entity;

import fr.asipsante.platines.entity.listeners.GlobalListener;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.springframework.security.acls.model.Sid;

/**
 * Class Certificate corresponding to the table "certificat" in the database.
 *
 * @author apierre
 */
@Entity
@Table(name = "certificat")
@EntityListeners(GlobalListener.class)
public class Certificate extends AbstractEntity implements Serializable {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 0L;

  /** Certificate pem. */
  @Column(name = "pem", columnDefinition = "text")
  private String pem;

  /** Certificate file name. */
  @Column(name = "nomFichier")
  private String fileName;

  /** Certificate file. */
  @Lob
  @Basic(fetch = FetchType.LAZY)
  @Column(name = "fichier", columnDefinition = "longblob")
  private byte[] file;

  /** validity date. */
  @Column(name = "validity_date")
  private Date validityDate;

  /** Chain of trust associate to the certificate. */
  @ManyToOne
  @JoinColumn(name = "CHAINEDECONFIANCE_ID", nullable = false)
  private ChainOfTrust chainOfTrust;

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
   * @param pem the pem
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
   * @param fileName the file name
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
   * @param file the file
   */
  public void setFile(byte[] file) {
    this.file = file;
  }

  /**
   * Gets validity date.
   *
   * @return the validyDate
   */
  public Date getValidityDate() {
    return validityDate;
  }

  /**
   * Sets validity date.
   *
   * @param validityDate the validity date
   */
  public void setValidityDate(Date validityDate) {
    this.validityDate = validityDate;
  }

  /**
   * Gets the certificate chain of trust.
   *
   * @return the chain of trust
   */
  public ChainOfTrust getChainOfTrust() {
    return chainOfTrust;
  }

  /**
   * Sets the certificate chain of trust.
   *
   * @param chainOfTrust the chain of trust
   */
  public void setChainOfTrust(ChainOfTrust chainOfTrust) {
    this.chainOfTrust = chainOfTrust;
  }

  @Override
  public Sid getOwner() {
    return null;
  }
}
