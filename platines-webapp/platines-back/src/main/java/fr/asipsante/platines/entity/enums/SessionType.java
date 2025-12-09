/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.entity.enums;

/**
 * Enum of Session Types.
 *
 * @author apierre
 */
public enum SessionType {

  /** Type sandbox. */
  SANDBOX("bac à sable"),

  /** Type recipe. */
  RECIPE("recette"),

  /** Type APPROVAL. */
  APPROVAL("homologation");

  /** value. */
  private String value;

  /**
   * constructeur.
   *
   * @param value
   */
  private SessionType(String value) {
    this.value = value;
  }

  /**
   * @return the value.
   */
  public String getValue() {
    return value;
  }
}
