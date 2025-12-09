/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.entity.enums;

import com.google.gson.annotations.SerializedName;

/**
 * Enum of the property types.
 *
 * @author apierre
 */
public enum PropertyType {

  /** Type non editable and visible. */
  @SerializedName(value = "NON_EDITABLE_VISIBLE")
  NON_EDITABLE_VISIBLE("Non éditable visible"),

  /** Type editable and visible. */
  @SerializedName(value = "EDITABLE_VISIBLE")
  EDITABLE_VISIBLE("Editable visible"),

  /** Type non editable and invisible. */
  @SerializedName(value = "NON_EDITABLE_INVISIBLE")
  NON_EDITABLE_INVISIBLE("Non éditable invisible"),

  /** Type endpoint. */
  @SerializedName(value = "ENDPOINT")
  ENDPOINT("Endpoint");

  /** value. */
  private String value;

  /**
   * constructeur.
   *
   * @param value
   */
  private PropertyType(String value) {
    this.value = value;
  }

  /**
   * @return the value
   */
  public String getValue() {
    return value;
  }
}
