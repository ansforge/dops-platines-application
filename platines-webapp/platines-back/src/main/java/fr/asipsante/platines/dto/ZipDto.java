/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.dto;

/**
 * The type Zip dto.
 *
 * @author apierre
 */
public class ZipDto {

  /** zip. */
  private byte[] zip;

  /** constructeur par défaut. */
  public ZipDto() {
    super();
  }

  /**
   * constructeur plein.
   *
   * @param zip the zip
   */
  public ZipDto(byte[] zip) {
    super();
    this.zip = zip;
  }

  /**
   * Get zip byte [ ].
   *
   * @return the zip
   */
  public byte[] getZip() {
    return zip;
  }

  /**
   * Sets zip.
   *
   * @param zip the zip to set
   */
  public void setZip(byte[] zip) {
    this.zip = zip;
  }
}
