/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.dto;

/**
 * @author aboittiaux
 */
public class ChainOfTrustListDto {

  /** Chain of trust id. */
  private Long id;

  /** Chain of trust name. */
  private String name;

  /** Chain of trust description. */
  private String description;

  /** validity. */
  private boolean valide;

  /** owner. */
  private UserDto user;

  /** Default constructor. */
  public ChainOfTrustListDto() {
    super();
  }

  /**
   * Gets the chain of trust id.
   *
   * @return the chain of trust id
   */
  public Long getId() {
    return id;
  }

  /**
   * Sets the chain of trust id.
   *
   * @param id, the new chain of trust id to set
   */
  public void setId(Long id) {
    this.id = id;
  }

  /**
   * Gets the chain of trust description.
   *
   * @return the chain of trust description
   */
  public String getDescription() {
    return description;
  }

  /**
   * Sets the chain of trust description.
   *
   * @param description, the new chain of trust description to set
   */
  public void setDescription(String description) {
    this.description = description;
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
   * @return the valide
   */
  public boolean isValide() {
    return valide;
  }

  /**
   * @param valide the valide to set
   */
  public void setValide(boolean valide) {
    this.valide = valide;
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
