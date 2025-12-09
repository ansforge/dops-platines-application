/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.dto;

import fr.asipsante.platines.entity.enums.CertificateState;
import java.util.Date;
import lombok.Data;

/**
 * @author apierre
 */
@Data
public class TestCertificateDto {

  /** The testCertificate id. */
  private Long id;

  /** The certificate file name. */
  private String fileName;

  /** The certificate pem. */
  private byte[] file;

  /** The certificate description. */
  private String description;

  /** The certificate is downloadable. */
  private boolean downloadable;

  /** The certificate state. */
  private CertificateState state;

  /** The certificate validity date. */
  private Date validityDate;

  /** The certificate type (CLIENT, SERVER or SIGNATURE). */
  private String type;

  /** Default constructor. */
  public TestCertificateDto() {
    super();
  }
}
