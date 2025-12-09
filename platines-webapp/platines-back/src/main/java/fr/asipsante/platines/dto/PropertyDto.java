/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.dto;

import fr.asipsante.platines.entity.enums.PropertyType;

/**
 * @author aboittiaux
 */
public class PropertyDto {

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

  /** Constructeur. */
  public PropertyDto() {
    super();
  }

  /**
   * @return the id
   */
  public Long getId() {
    return id;
  }

  /**
   * @param id the id to set
   */
  public void setId(Long id) {
    this.id = id;
  }

  /**
   * @return the key
   */
  public String getKey() {
    return key;
  }

  /**
   * @param key the key to set
   */
  public void setKey(String key) {
    this.key = key;
  }

  /**
   * @return the value
   */
  public String getValue() {
    return value;
  }

  /**
   * @param value the value to set
   */
  public void setValue(String value) {
    this.value = value;
  }

  /**
   * @return the description
   */
  public String getDescription() {
    return description;
  }

  /**
   * @param description the description to set
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * @return the propertyType
   */
  public PropertyType getPropertyType() {
    return propertyType;
  }

  /**
   * @param propertyType the propertyType to set
   */
  public void setPropertyType(PropertyType propertyType) {
    this.propertyType = propertyType;
  }
}
