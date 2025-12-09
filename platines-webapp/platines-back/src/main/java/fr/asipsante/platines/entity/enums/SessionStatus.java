/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.entity.enums;

/**
 * Enum of the session status.
 *
 * @author apierre
 */
public enum SessionStatus {

  /** Statut non execute. */
  NONEXECUTE("non exécuté"),

  /** Statut en construction. */
  CONSTRUCT("en cours de préparation"),

  /** Statut en cours de déploiement. */
  DEPLOYMENT("en cours de déploiement"),

  /** Statut en cours de démarrage */
  STARTING("en cours de démarrage"),

  /** Statut pending. */
  PENDING("en cours d'exécution"),

  /** Statut error. */
  ERROR("erreur"),

  /** Statut finished. */
  CANCELED("annulée"),

  /** Statut finished. */
  FINISHED("fini"),

  /** Statut waiting. */
  WAITING("File d'attente");

  /** value. */
  private String value;

  /**
   * constructeur.
   *
   * @param value
   */
  private SessionStatus(String value) {
    this.value = value;
  }

  /**
   * @return the value.
   */
  public String getValue() {
    return value;
  }
}
