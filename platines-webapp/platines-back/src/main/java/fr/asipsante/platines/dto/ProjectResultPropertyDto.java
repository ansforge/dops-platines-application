/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.dto;

import fr.asipsante.platines.entity.enums.PropertyType;

/**
 * @author apierre
 */
public class ProjectResultPropertyDto {

  /** The property id. */
  private Long id;

  /** The property key. */
  private String key;

  /** The property value. */
  private String value;

  /** The property description. */
  private String description;

  /** The property type. */
  private PropertyType propertyType;

  /**
   * Gets the property id.
   *
   * @return the id
   */
  public Long getId() {
    return id;
  }

  /**
   * Sets the property id.
   *
   * @param id, the new id to set
   */
  public void setId(Long id) {
    this.id = id;
  }

  /**
   * Gets the property key.
   *
   * @return the key
   */
  public String getKey() {
    return key;
  }

  /**
   * Sets the property key.
   *
   * @param key, the new key to set
   */
  public void setKey(String key) {
    this.key = key;
  }

  /**
   * Gets the property value.
   *
   * @return the value
   */
  public String getValue() {
    return value;
  }

  /**
   * Sets the property value.
   *
   * @param value, the new value to set
   */
  public void setValue(String value) {
    this.value = value;
  }

  /**
   * Gets the property description.
   *
   * @return the description
   */
  public String getDescription() {
    return description;
  }

  /**
   * Sets the property description.
   *
   * @param description, the new description to set
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Gets the property type.
   *
   * @return the property type
   */
  public PropertyType getPropertyType() {
    return propertyType;
  }

  /**
   * Sets the property type.
   *
   * @param propertyType, the new property type to set
   */
  public void setPropertyType(PropertyType propertyType) {
    this.propertyType = propertyType;
  }
}
