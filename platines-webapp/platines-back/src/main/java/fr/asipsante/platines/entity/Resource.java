/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.entity;

import fr.asipsante.platines.entity.enums.FileType;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.springframework.security.acls.model.Sid;

/**
 * Class Resource corresponding to the table "resource" in the database.
 *
 * @author cnader
 */
@Entity
@Table(name = "resource")
// @EntityListeners(GlobalListener.class)
public class Resource extends AbstractEntity implements Serializable {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 834770155882542828L;

  /** The resource fileName. */
  @Column(name = "fileName")
  private String fileName;

  /** The resource file. */
  @Lob
  @Basic(fetch = FetchType.LAZY)
  @Column(name = "file")
  private byte[] file;

  /** The file type. */
  @Enumerated(EnumType.STRING)
  @Column(name = "filetype")
  private FileType fileType;

  /** Upload date of the resource file. */
  @Column(name = "date_upload")
  private Date dateUpload;

  /** The resource association. */
  @ManyToOne
  @JoinColumn(name = "ASSOCIATION_ID", nullable = false)
  private Association association;

  /** The resource theme. */
  @ManyToOne
  @JoinColumn(name = "FAMILY_ID", nullable = false)
  private Theme theme;

  /**
   * Gets the resource association.
   *
   * @return the association
   */
  public Theme getTheme() {
    return theme;
  }

  /**
   * Sets the resource theme.
   *
   * @param theme, the new theme to set
   */
  public void setTheme(Theme theme) {
    this.theme = theme;
  }

  /**
   * Gets the resource association.
   *
   * @return the association
   */
  public Association getAssociation() {
    return association;
  }

  /**
   * Sets the resource association.
   *
   * @param association, the new association to set
   */
  public void setAssociation(Association association) {
    this.association = association;
  }

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

  /**
   * Gets the file type.
   *
   * @return the fileType
   */
  public FileType getFileType() {
    return fileType;
  }

  /**
   * Sets the file type.
   *
   * @param fileType, the fileType to set
   */
  public void setFileType(FileType fileType) {
    this.fileType = fileType;
  }

  @Override
  public Sid getOwner() {
    return null;
  }
}
