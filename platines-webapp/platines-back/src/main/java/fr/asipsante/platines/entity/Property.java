/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.entity;

import fr.asipsante.platines.entity.enums.PropertyType;
import fr.asipsante.platines.entity.listeners.GlobalListener;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import org.springframework.security.acls.model.Sid;

/**
 * Class Property corresponding to the table "proprietes" in the database.
 *
 * @author apierre
 */
@Entity
@Table(name = "proprietes")
@EntityListeners(GlobalListener.class)
public class Property extends AbstractEntity implements Serializable {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 0L;

  /** The property key. */
  @Column(name = "cle", length = 50)
  private String key;

  /** The property value. */
  @Column(name = "valeur", columnDefinition = "text")
  private String value;

  /** The property description. */
  @Column(name = "description", columnDefinition = "text")
  private String description;

  /** The property type. */
  @Enumerated(EnumType.STRING)
  @Column(name = "propertyType", length = 50)
  private PropertyType propertyType;

  /**
   * Gets the property key.
   *
   * @return the property key
   */
  public String getKey() {
    return key;
  }

  /**
   * Sets the property key.
   *
   * @param key, the new property key
   */
  public void setKey(String key) {
    this.key = key;
  }

  /**
   * Gets the property value.
   *
   * @return the property value
   */
  public String getValue() {
    return value;
  }

  /**
   * Sets the property value.
   *
   * @param value, the new property value
   */
  public void setValue(String value) {
    this.value = value;
  }

  /**
   * Gets the property description.
   *
   * @return the property description
   */
  public String getDescription() {
    return description;
  }

  /**
   * Sets the property description.
   *
   * @param description, the new description
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Gets the property type.
   *
   * @return the propertyType
   */
  public PropertyType getPropertyType() {
    return propertyType;
  }

  /**
   * Sets the property type.
   *
   * @param propertyType, the new propertyType to set
   */
  public void setPropertyType(PropertyType propertyType) {
    this.propertyType = propertyType;
  }

  @Override
  public Sid getOwner() {
    return null;
  }
}
