/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.entity.enums;

/**
 * Enum of the result status.
 *
 * @author apierre
 */
public enum ResultStatus {

  /** Status non execute. */
  NONEXECUTE("non exécuté"),

  /** Status pending. */
  PENDING("en cours"),

  /** Status success. */
  SUCCESS("succès"),

  /** Status failure. */
  FAILURE("échec"),

  /** Status error. */
  ERROR("erreur"),

  /** Status waiting. */
  WAITING("en attente");

  /** value. */
  private String value;

  /**
   * constructeur.
   *
   * @param value
   */
  private ResultStatus(String value) {
    this.value = value;
  }

  /**
   * @return the value.
   */
  public String getValue() {
    return value;
  }
}
