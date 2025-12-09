/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.executor.model;

/**
 * Récupération des test Step du projet.
 *
 * @author aboittiaux
 */
public class TestStepDetail {

  /** Nom du testStep. */
  private String name;

  /** Description du testStep. */
  private String description;

  /** Constructeur par défaut. */
  public TestStepDetail() {
    super();
  }

  /**
   * Instantiates a new Test step detail.
   *
   * @param name the name
   * @param description the description
   */
  public TestStepDetail(String name, String description) {
    super();
    this.name = name;
    this.description = description;
  }

  /**
   * Gets name.
   *
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * Sets name.
   *
   * @param name the name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Gets description.
   *
   * @return the description
   */
  public String getDescription() {
    return description;
  }

  /**
   * Sets description.
   *
   * @param description the description to set
   */
  public void setDescription(String description) {
    this.description = description;
  }
}
