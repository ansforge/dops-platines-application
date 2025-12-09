/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.dto;

/**
 * @author cnader
 */
public class ResourceBytesDto extends ResourceDto {

  private byte[] file;

  /** File mimeType. */
  private String mimeType;

  public byte[] getFile() {
    return file;
  }

  public void setFile(byte[] file) {
    this.file = file;
  }

  public String getMimeType() {
    return mimeType;
  }

  public void setMimeType(String mimeType) {
    this.mimeType = mimeType;
  }
}
