/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.executor.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe représentant les informations contenues dans les tests Suite du projet.
 *
 * @author aboittiaux
 */
public class TestSuiteDetail {

  /** The id. */
  private Long id;

  /** Nom du testSuite. */
  private String name;

  /** Description du testSuite. */
  private String description;

  /** Liste des testCases pour ce testSuite. */
  private List<TestCaseDetail> listTestCase = new ArrayList<>();

  /** Constructeur par défaut. */
  public TestSuiteDetail() {
    super();
  }

  /**
   * Instantiates a new test suite detail.
   *
   * @param name the name
   * @param description the description
   * @param listTestCase the list test case
   */
  public TestSuiteDetail(String name, String description, List<TestCaseDetail> listTestCase) {
    super();
    this.name = name;
    this.description = description;
    this.listTestCase = listTestCase;
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
   * Gets the list test case.
   *
   * @return the listTestCase
   */
  public List<TestCaseDetail> getListTestCase() {
    return listTestCase;
  }

  /**
   * Sets the list test case.
   *
   * @param listTestCase the listTestCase to set
   */
  public void setListTestCase(List<TestCaseDetail> listTestCase) {
    this.listTestCase = listTestCase;
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
}
