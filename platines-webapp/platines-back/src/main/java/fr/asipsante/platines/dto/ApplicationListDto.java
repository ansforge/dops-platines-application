/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.dto;

import fr.asipsante.platines.entity.enums.Role;

/**
 * @author apierre
 */
public class ApplicationListDto {

  /** application id. */
  private Long id;

  /** application name. */
  private String name;

  /** application version. */
  private String version;

  /** application description. */
  private String description;

  /** application role. */
  private Role role;

  /** application owner. */
  private UserDto user;

  /** constructeur par défaut. */
  public ApplicationListDto() {
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
   * @return the version
   */
  public String getVersion() {
    return version;
  }

  /**
   * @param version the version to set
   */
  public void setVersion(String version) {
    this.version = version;
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
   * @return the user
   */
  public UserDto getUser() {
    return user;
  }

  /**
   * @param user the user to set
   */
  public void setUser(UserDto user) {
    this.user = user;
  }
}
