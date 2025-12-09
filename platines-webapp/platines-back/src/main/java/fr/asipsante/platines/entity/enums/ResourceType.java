/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.entity.enums;

import com.google.gson.annotations.SerializedName;

/**
 * Enum of the property types.
 *
 * @author cnader
 */
public enum ResourceType {

  /** Type documentation. */
  @SerializedName(value = "DOCUMENTATION")
  DOCUMENTATION("Documentation"),

  /** Type bundle jar. */
  @SerializedName(value = "BUNDLE_JAR")
  BUNDLE_JAR("Bundle jar"),

  /** Type nomenclature. */
  @SerializedName(value = "NOMENCLATURE")
  NOMENCLATURE("Nomenclature"),

  /** Type resource. */
  @SerializedName(value = "RESOURCE")
  RESOURCE("Ressource"),

  /** Type plugin. */
  @SerializedName(value = "PLUGIN")
  PLUGIN("Plugin"),

  /** Type single jar. */
  @SerializedName(value = "SINGLE_JAR")
  SINGLE_JAR("Single jar");

  /** value. */
  private String value;

  /**
   * constructeur.
   *
   * @param value
   */
  private ResourceType(String value) {
    this.value = value;
  }

  /**
   * @return the value
   */
  public String getValue() {
    return value;
  }
}
