/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.entity;

import fr.asipsante.platines.entity.listeners.VisibilityListener;
import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * Class Version corresponding to the table "version" in the database.
 *
 * @author apierre
 */
@Entity
@Table(
    name = "version",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"libelle", "systeme_id"})})
@DiscriminatorValue("3")
@EntityListeners(VisibilityListener.class)
public class Version extends Association implements Serializable {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 9151407970237023295L;

  /** The version label. */
  @Column(name = "libelle", length = 50)
  private String label;

  /** The version description. */
  @Column(name = "description", length = 500)
  private String description;

  /** The version visibility. */
  @Column(name = "visibilite")
  private boolean visibility;

  /** The version service. */
  @ManyToOne
  @JoinColumn(name = "systeme_id", nullable = false)
  private SimulatedService service;

  /** The tests projects. */
  @ManyToMany(fetch = FetchType.LAZY) /*Ce backlink n'est pas systématiquement utilisé
                                         * et pourrait augmenter inutilement la taille
                                         * des grappes chargées */
  @JoinTable(
      name = "version_project",
      joinColumns = {@JoinColumn(name = "VERSION_ID", nullable = true, updatable = true)},
      inverseJoinColumns = {@JoinColumn(name = "PROJECT_ID", nullable = true, updatable = true)})
  private List<Project> projects;

  /**
   * Gets label.
   *
   * @return the label
   */
  public String getLabel() {
    return label;
  }

  /**
   * Sets the version label.
   *
   * @param label the label
   */
  public void setLabel(String label) {
    this.label = label;
  }

  /**
   * Gets the version description.
   *
   * @return the version description
   */
  public String getDescription() {
    return description;
  }

  /**
   * Sets the version description.
   *
   * @param description the description
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Gets the version service.
   *
   * @return the version service
   */
  public SimulatedService getService() {
    return service;
  }

  /**
   * Sets the version service.
   *
   * @param service the service
   */
  public void setService(SimulatedService service) {
    this.service = service;
  }

  /**
   * Gets the projects.
   *
   * @return the projects
   */
  public List<Project> getProjects() {
    return projects;
  }

  /**
   * Sets the projects.
   *
   * @param projects the projects
   */
  public void setProjects(List<Project> projects) {
    this.projects = projects;
  }

  /**
   * Get the version visibility.
   *
   * @return the version visibility
   */
  public boolean getVisibility() {
    return visibility;
  }

  /**
   * Sets the version visibility.
   *
   * @param visibility the visibility
   */
  public void setVisibility(boolean visibility) {
    this.visibility = visibility;
  }
}
