/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.Table;
import org.springframework.security.acls.model.Sid;

/**
 * Class Nomenclature corresponding to the table "nomenclature" in the database.
 *
 * @author apierre
 */
@Entity
@Table(name = "nomenclature")
public class Nomenclature extends AbstractEntity implements Serializable {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;

  /** The nomenclature fileName. */
  @Column(name = "fileName")
  private String fileName;

  /** The nomenclature file. */
  @Lob
  @Basic(fetch = FetchType.LAZY)
  @Column(name = "file", columnDefinition = "longblob")
  private byte[] file;

  /** Upload date of the nomenclature file. */
  @Column(name = "dateUpload")
  private Date dateUpload;

  /**
   * Gets the file name.
   *
   * @return the fileName
   */
  public String getFileName() {
    return fileName;
  }

  /**
   * Sets the file name.
   *
   * @param fileName, the new fileName to set
   */
  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  /**
   * Gets the nomenclature file.
   *
   * @return the file
   */
  public byte[] getFile() {
    return file;
  }

  /**
   * Sets the nomenclature file.
   *
   * @param file, the new file to set
   */
  public void setFile(byte[] file) {
    this.file = file;
  }

  /**
   * Gets the upload date of the nomenclature file.
   *
   * @return the dateUpload
   */
  public Date getDateUpload() {
    return dateUpload;
  }

  /**
   * Sets the upload date.
   *
   * @param dateUpload, the dateUpload to set
   */
  public void setDateUpload(Date dateUpload) {
    this.dateUpload = dateUpload;
  }

  @Override
  public Sid getOwner() {
    return null;
  }
}
