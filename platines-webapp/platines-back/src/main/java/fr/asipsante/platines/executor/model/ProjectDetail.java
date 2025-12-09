/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.executor.model;

import fr.asipsante.platines.dto.PropertyDto;
import fr.asipsante.platines.entity.enums.Role;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Récupération des détails du projet.
 *
 * @author aboittiaux
 */
public class ProjectDetail {

  /** Nom du projet sur la plateforme. */
  private String name;

  /** Nom du projet dans SoapUi. */
  private String nameProject;

  /** Description du projet. */
  private String description;

  /** Liste des propriétés custom. */
  private Set<PropertyDto> properties = new HashSet<>();

  /** Liste des fichiers associés au projet. */
  private Set<String> fileList = new HashSet<>();

  /** Liste des tests suites du projet. */
  private List<TestSuiteDetail> testSuiteDetail = new ArrayList<>();

  /** Role du projet. */
  private Role role;

  /** Constructeur par défaut. */
  public ProjectDetail() {
    super();
  }

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * @param name the name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * @return the nameProject
   */
  public String getNameProject() {
    return nameProject;
  }

  /**
   * @param nameProject the nameProject to set
   */
  public void setNameProject(String nameProject) {
    this.nameProject = nameProject;
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
   * @return the properties
   */
  public Set<PropertyDto> getProperties() {
    return properties;
  }

  /**
   * @param properties the properties to set
   */
  public void setProperties(Set<PropertyDto> properties) {
    this.properties = properties;
  }

  /**
   * @return the fileList
   */
  public Set<String> getFileList() {
    return fileList;
  }

  /**
   * @param fileList the fileList to set
   */
  public void setFileList(Set<String> fileList) {
    this.fileList = fileList;
  }

  /**
   * @return the testSuiteDetail
   */
  public List<TestSuiteDetail> getTestSuiteDetail() {
    return testSuiteDetail;
  }

  /**
   * @param testSuiteDetail the testSuiteDetail to set
   */
  public void setTestSuiteDetail(List<TestSuiteDetail> testSuiteDetail) {
    this.testSuiteDetail = testSuiteDetail;
  }

  /**
   * @return the role
   */
  public Role getRole() {
    return role;
  }

  /**
   * @param role the role to set
   */
  public void setRole(Role role) {
    this.role = role;
  }
}
