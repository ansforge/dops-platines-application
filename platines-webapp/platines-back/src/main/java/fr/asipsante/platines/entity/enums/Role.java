/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.entity.enums;

/**
 * The Enum Role.
 *
 * @author apierre
 */
public enum Role {

  /** Application client. */
  CLIENT("Client"),
  /** Application server. */
  SERVER("Serveur");

  /** value. */
  private String value;

  /**
   * constructeur.
   *
   * @param value
   */
  private Role(String value) {
    this.value = value;
  }

  /**
   * @return the value
   */
  public String getValue() {
    return value;
  }
}
