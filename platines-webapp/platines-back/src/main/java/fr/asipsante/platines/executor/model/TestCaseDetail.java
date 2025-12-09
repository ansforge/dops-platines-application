/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.executor.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Récupération des test Case du projet.
 *
 * @author aboittiaux
 */
public class TestCaseDetail {

  /** The id. */
  private Long id;

  /** Nom du testCase. */
  private String name;

  /** Description du testCase. */
  private String description;

  /** Criticité du testCase. */
  private String criticality;

  /** Liste des testSteps contenu dans le testCase. */
  private List<TestStepDetail> listTestSteps = new ArrayList<>();

  /** response name. */
  private String responseName;

  /** Constructeur par defaut. */
  public TestCaseDetail() {
    super();
  }

  /**
   * Instantiates a new test case detail.
   *
   * @param id, the test case id
   * @param name, the name
   * @param description, the description
   * @param listTestSteps, the list test steps
   */
  public TestCaseDetail(
      Long id, String name, String description, List<TestStepDetail> listTestSteps) {
    super();
    this.id = id;
    this.name = name;
    this.description = description;
    this.listTestSteps = listTestSteps;
  }

  /**
   * Gets the name.
   *
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the name.
   *
   * @param name the name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Gets the description.
   *
   * @return the description
   */
  public String getDescription() {
    return description;
  }

  /**
   * Sets the description.
   *
   * @param description the description to set
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Gets the criticality.
   *
   * @return the criticality
   */
  public String getCriticality() {
    return criticality;
  }

  /**
   * Sets the criticality.
   *
   * @param criticality the criticality to set
   */
  public void setCriticality(String criticality) {
    this.criticality = criticality;
  }

  /**
   * Gets the list test steps.
   *
   * @return the listTestSteps
   */
  public List<TestStepDetail> getListTestSteps() {
    return listTestSteps;
  }

  /**
   * Sets the list test steps.
   *
   * @param listTestSteps the listTestSteps to set
   */
  public void setListTestSteps(List<TestStepDetail> listTestSteps) {
    this.listTestSteps = listTestSteps;
  }

  /**
   * Gets the id.
   *
   * @return the id
   */
  public Long getId() {
    return id;
  }

  /**
   * Sets the id.
   *
   * @param id the new id
   */
  public void setId(Long id) {
    this.id = id;
  }

  /**
   * @return the responseName
   */
  public String getResponseName() {
    return responseName;
  }

  /**
   * @param responseName the responseName to set
   */
  public void setResponseName(String responseName) {
    this.responseName = responseName;
  }
}
