/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.dto;

/**
 * @author aboittiaux
 */
public class FileDownloaded {

  /** file. */
  private byte[] file;

  /** file name. */
  private String fileName;

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
}
