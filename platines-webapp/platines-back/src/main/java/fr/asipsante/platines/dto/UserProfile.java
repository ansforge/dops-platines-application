/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.dto;

/**
 * @author aboittiaux
 */
public class UserProfile {

  /** password. */
  private PasswordSettings identification;

  /** user. */
  private UserDto user;

  /**
   * @return the identification
   */
  public PasswordSettings getIdentification() {
    return identification;
  }

  /**
   * @param identification the identification to set
   */
  public void setIdentification(PasswordSettings identification) {
    this.identification = identification;
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
