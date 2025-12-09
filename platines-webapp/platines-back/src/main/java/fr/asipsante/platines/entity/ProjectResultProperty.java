/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.entity;

import fr.asipsante.platines.entity.enums.PropertyType;
import fr.asipsante.platines.entity.listeners.SessionListener;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.Sid;

/**
 * Class ProjectResultProperty corresponding to the table "propriete_session" in the database.
 *
 * @author apierre
 */
@Entity
@Table(name = "propriete_session")
@EntityListeners(SessionListener.class)
public class ProjectResultProperty extends AbstractEntity implements Serializable {

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

  /** the project result. */
  @ManyToOne
  @JoinColumn(name = "RPROJET_ID", nullable = true)
  private ProjectResult projectResult;

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
   * @return the projectResult
   */
  public ProjectResult getProjectResult() {
    return projectResult;
  }

  /**
   * @param projectResult the projectResult to set
   */
  public void setProjectResult(ProjectResult projectResult) {
    this.projectResult = projectResult;
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

  @Override
  public Sid getOwner() {
    return new PrincipalSid(
        getProjectResult().getTestSession().getApplication().getUser().getMail());
  }
}
