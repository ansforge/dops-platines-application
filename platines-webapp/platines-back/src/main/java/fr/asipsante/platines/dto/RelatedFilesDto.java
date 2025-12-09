/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.dto;

import fr.asipsante.platines.entity.enums.FileType;
import java.util.Date;

/**
 * @author aboittiaux
 */
public class RelatedFilesDto {

  /** The file id. */
  private Long id;

  /** The file name. */
  private String fileName;

  /** The file. */
  private byte[] file;

  /** The file type. */
  private FileType fileType;

  /** File mimeType. */
  private String mimeType;

  /** upload date. */
  private Date dateUpload;

  /** Constructeur. */
  public RelatedFilesDto() {
    super();
  }

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
   * @return the file
   */
  public byte[] getFile() {
    return file;
  }

  /**
   * @param file the file to set
   */
  public void setFile(byte[] file) {
    this.file = file;
  }

  /**
   * @return the fileType
   */
  public FileType getFileType() {
    return fileType;
  }

  /**
   * @param fileType the fileType to set
   */
  public void setFileType(FileType fileType) {
    this.fileType = fileType;
  }

  /**
   * @return the mimeType
   */
  public String getMimeType() {
    return mimeType;
  }

  /**
   * @param mimeType the mimeType to set
   */
  public void setMimeType(String mimeType) {
    this.mimeType = mimeType;
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
}
