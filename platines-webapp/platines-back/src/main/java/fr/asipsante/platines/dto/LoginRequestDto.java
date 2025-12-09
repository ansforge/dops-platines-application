/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.dto;

/**
 * @author aboittiaux
 */
public class LoginRequestDto {
  /** The login. */
  private String login;

  /** The password. */
  private String password;

  /** The password. */
  private String token;

  /**
   * Gets the login.
   *
   * @return the login
   */
  public String getLogin() {
    return login;
  }

  /**
   * Sets the login.
   *
   * @param login the login to set
   */
  public void setLogin(String login) {
    this.login = login;
  }

  /**
   * Gets the password.
   *
   * @return the password
   */
  public String getPassword() {
    return password;
  }

  /**
   * Sets the password.
   *
   * @param password the password to set
   */
  public void setPassword(String password) {
    this.password = password;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }
}
