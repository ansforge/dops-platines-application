/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.entity.enums;

/**
 * Enum of the file types.
 *
 * @author apierre
 */
public enum FileType {

  /** File used in tests. */
  RESOURCE("ressource"),

  /** Doc file, not used in tests. */
  DOCUMENT("document"),

  /** external lib used in tests */
  SINGLE_JAR("single_jar"),

  BUNDLE_JAR("bundle_jar"),

  PLUGIN("plugin"),

  NOMENCLATURE("nomenclature");

  /** value. */
  private String value;

  /**
   * constructeur.
   *
   * @param value
   */
  private FileType(String value) {
    this.value = value;
  }

  /**
   * @return the value.
   */
  public String getValue() {
    return value;
  }
}
