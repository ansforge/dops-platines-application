/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.entity.enums;

/**
 * Enum of the certificate states.
 *
 * @author apierre
 */
public enum CertificateState {

  /** State valid. */
  VALID("valide"),

  /** State obsolete. */
  OBSOLETE("expiré"),

  /** State revocated. */
  REVOCATED("révoqué"),

  /** State invalid. */
  INVALID("invalide");

  /** value. */
  private String value;

  /**
   * constructeur.
   *
   * @param value
   */
  private CertificateState(String value) {
    this.value = value;
  }

  /**
   * @return the value.
   */
  public String getValue() {
    return value;
  }
}
