/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.entity;

import fr.asipsante.platines.entity.enums.CertificateState;
import fr.asipsante.platines.entity.listeners.VisibilityListener;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.Table;
import org.springframework.security.acls.model.Sid;

/** Class TestCertificate corresponding to the table "certificat_tst" in the database. */
@Entity
@Table(name = "certificat_tst")
@EntityListeners(VisibilityListener.class)
public class TestCertificate extends AbstractEntity implements Serializable {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 0L;

  /** The certificate file name. */
  @Column(name = "nomFichier")
  private String fileName;

  /** The certificate pem. */
  @Lob
  @Basic(fetch = FetchType.LAZY)
  @Column(name = "fichier", columnDefinition = "longblob")
  private byte[] file;

  /** The certificate description. */
  @Column(name = "description", length = 500)
  private String description;

  /** The certificate is downloadable. */
  @Column(name = "telechargeable")
  private boolean downloadable;

  /** The certificate state. */
  @Enumerated(EnumType.STRING)
  @Column(name = "etat", length = 25)
  private CertificateState state;

  /** the certificate validity date. */
  @Column(name = "validityDate")
  private Date validityDate;

  /** The certificate type (CLIENT, SERVER or SIGNATURE). */
  @Column(name = "type", length = 25)
  private String type;

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
   * @param fileName, the new certificate file name to set
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
   * @param file, the new file to set
   */
  public void setFile(byte[] file) {
    this.file = file;
  }

  /**
   * Gets the description.
   *
   * @return the description
   */
  public String getDescription() {
    return description;
  }

  /**
   * Sets the description.
   *
   * @param description the new description
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Checks if is downloadable.
   *
   * @return true if is downloadable, false else
   */
  public boolean isDownloadable() {
    return downloadable;
  }

  /**
   * Sets the boolean downloadable.
   *
   * @param downloadable, the new value to set
   */
  public void setDownloadable(boolean downloadable) {
    this.downloadable = downloadable;
  }

  public boolean getVisibility() {
    return downloadable;
  }

  /**
   * Gets the certificate state.
   *
   * @return the certificate state
   */
  public CertificateState getState() {
    return state;
  }

  /**
   * Sets the certificate state.
   *
   * @param state, the new certificate state
   */
  public void setState(CertificateState state) {
    this.state = state;
  }

  /**
   * Gets the certificate validity date.
   *
   * @return the certificate validity date
   */
  public Date getValidityDate() {
    return validityDate;
  }

  /**
   * Sets the certificate validity date.
   *
   * @param validityDate, the new validity date to set
   */
  public void setValidityDate(Date validityDate) {
    this.validityDate = validityDate;
  }

  /**
   * Gets the certificate type.
   *
   * @return the certificate type
   */
  public String getType() {
    return type;
  }

  /**
   * Sets the certificate type.
   *
   * @param type, the new certificate type to set
   */
  public void setType(String type) {
    this.type = type;
  }

  @Override
  public Sid getOwner() {
    return null;
  }
}
