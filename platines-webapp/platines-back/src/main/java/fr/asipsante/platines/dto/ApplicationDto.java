/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.dto;

import fr.asipsante.platines.entity.enums.Role;

/**
 * @author apierre
 */
public class ApplicationDto {

  /** application id. */
  private Long id;

  /** Application name. */
  private String name;

  /** Application version. */
  private String version;

  /** applicaiton description. */
  private String description;

  /** Application url. */
  private String url;

  /** Application role. */
  private Role role;

  /** Application owner. */
  private UserDto user;

  /** Chain of trust. */
  private ChainOfTrustListDto chainOfTrustDto;

  /** constructeur par défaut. */
  public ApplicationDto() {
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
   * @return the url
   */
  public String getUrl() {
    return url;
  }

  /**
   * @param url the url to set
   */
  public void setUrl(String url) {
    this.url = url;
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

  /**
   * @return the chainOfTrustDto
   */
  public ChainOfTrustListDto getChainOfTrustDto() {
    return chainOfTrustDto;
  }

  /**
   * @param chainOfTrustDto the chainOfTrustDto to set
   */
  public void setChainOfTrustDto(ChainOfTrustListDto chainOfTrustDto) {
    this.chainOfTrustDto = chainOfTrustDto;
  }
}
