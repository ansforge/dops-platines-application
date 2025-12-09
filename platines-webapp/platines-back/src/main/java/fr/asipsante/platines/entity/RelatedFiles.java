/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.entity;

import fr.asipsante.platines.entity.enums.FileType;
import fr.asipsante.platines.entity.listeners.GlobalListener;
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

/**
 * Class RelatedFile corresponding to the table "fichiers_associes" in the database.
 *
 * @author apierre
 */
@Entity
@Table(name = "fichiers_associes")
@EntityListeners(GlobalListener.class)
public class RelatedFiles extends AbstractEntity implements Serializable {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;

  /** The file name. */
  @Column(name = "fileName", length = 255)
  private String fileName;

  /** The file. */
  @Lob
  @Basic(fetch = FetchType.LAZY)
  @Column(name = "file")
  private byte[] file;

  /** The file type. */
  @Enumerated(EnumType.STRING)
  @Column(name = "filetype")
  private FileType fileType;

  /** upload date. */
  @Column(name = "date_upload")
  private Date dateUpload;

  /**
   * Gets the fileName.
   *
   * @return the fileName
   */
  public String getFileName() {
    return fileName;
  }

  /**
   * Sets the filename.
   *
   * @param fileName, the fileName to set
   */
  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  /**
   * Gets the file.
   *
   * @return the file
   */
  public byte[] getFile() {
    return file;
  }

  /**
   * Sets the file.
   *
   * @param file, the file to set
   */
  public void setFile(byte[] file) {
    this.file = file;
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

  /**
   * @return the dateUpload
   */
  public Date getDateUpload() {
    return dateUpload;
  }

  /**
   * @param dateUpload the dateUpload to set
   */
  public void setDateUpload(Date dateUpload) {
    this.dateUpload = dateUpload;
  }

  @Override
  public Sid getOwner() {
    return null;
  }
}
