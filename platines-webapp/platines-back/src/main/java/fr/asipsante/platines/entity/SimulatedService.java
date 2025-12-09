/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.entity;

import fr.asipsante.platines.entity.listeners.VisibilityListener;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * Class SimulatedService corresponding to the table "systeme" in the database. In the application
 * SimulatedService matches "WebService".
 *
 * @author apierre
 */
@Entity
@Table(
    name = "systeme",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"nom", "FAMILLE_ID"})})
@DiscriminatorValue("2")
@EntityListeners(VisibilityListener.class)
public class SimulatedService extends Association implements Serializable {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 5230470694380215535L;

  /** The service name. */
  @Column(name = "nom", length = 50)
  private String name;

  /** The service theme. */
  @ManyToOne
  @JoinColumn(name = "FAMILLE_ID", nullable = false)
  private Theme theme;

  /**
   * Gets the service name.
   *
   * @return the service name
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the service name.
   *
   * @param name, the name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Gets the service theme.
   *
   * @return the service theme
   */
  public Theme getTheme() {
    return theme;
  }

  /**
   * Sets the service theme.
   *
   * @param theme, the service theme to set
   */
  public void setTheme(Theme theme) {
    this.theme = theme;
  }

  // Méthode appelée par le listener pour positionner les ACL.
  public boolean getVisibility() {
    return true;
  }
}
