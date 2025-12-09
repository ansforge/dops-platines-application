/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.dto;

import fr.asipsante.platines.entity.enums.FileType;
import java.util.Date;

/**
 * @author cnader
 */
public class ResourceDto {

  /** The file id. */
  private Long id;

  /** The file name. */
  private String fileName;

  /** The file type. */
  private FileType fileType;

  /** The file type. */
  private String resourceType;

  /** upload date. */
  private Date dateUpload;

  /** Constructeur. */
  public ResourceDto() {
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
   * @return the resource type
   */
  public String getResourceType() {
    return resourceType;
  }

  /**
   * @param resourceType the resource type to set
   */
  public void setResourceType(String resourceType) {
    this.resourceType = resourceType;
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
