/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.dto;

import fr.asipsante.platines.entity.enums.Role;
import java.util.List;

/**
 * @author apierre
 */
public class ProjectSessionDto {

  /** project id. */
  private Long id;

  /** project name. */
  private String name;

  /** project description. */
  private String description;

  /** project file name. */
  private String fileName;

  /** project visibility. */
  private boolean visibility;

  /** project role. */
  private Role role;

  /** project versions. */
  private List<VersionDto> versions;

  /** project properties. */
  private List<PropertyDto> properties;

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
   * @return the fileName
   */
  public String getFileName() {
    return fileName;
  }

  /**
   * @param fileName the fileName to set
   */
  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  /**
   * @return the visibility
   */
  public boolean isVisibility() {
    return visibility;
  }

  /**
   * @param visibility the visibility to set
   */
  public void setVisibility(boolean visibility) {
    this.visibility = visibility;
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

  /**
   * @return the versions
   */
  public List<VersionDto> getVersions() {
    return versions;
  }

  /**
   * @param versions the versions to set
   */
  public void setVersions(List<VersionDto> versions) {
    this.versions = versions;
  }

  /**
   * @return the properties
   */
  public List<PropertyDto> getProperties() {
    return properties;
  }

  /**
   * @param properties the properties to set
   */
  public void setProperties(List<PropertyDto> properties) {
    this.properties = properties;
  }
}
