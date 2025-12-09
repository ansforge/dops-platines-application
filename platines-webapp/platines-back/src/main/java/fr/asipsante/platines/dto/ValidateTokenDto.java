/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.dto;

/**
 * The type Validate token dto.
 *
 * @author aboittiaux
 */
public class ValidateTokenDto {

  /** user email. */
  private String email;

  /** user profile. */
  private Long profile;

  /** user id. */
  private Long userid;

  /** token. */
  private String token;

  /**
   * Gets email.
   *
   * @return mail email
   */
  public String getEmail() {
    return email;
  }

  /**
   * Gets token.
   *
   * @return the token
   */
  public String getToken() {
    return token;
  }

  /**
   * Sets token.
   *
   * @param token the token to set
   */
  public void setToken(String token) {
    this.token = token;
  }

  /**
   * Sets email.
   *
   * @param email the email to set
   */
  public void setEmail(String email) {
    this.email = email;
  }

  /**
   * Gets profile.
   *
   * @return user profile
   */
  public Long getProfile() {
    return profile;
  }

  /**
   * Sets profile.
   *
   * @param profile the profile
   */
  public void setProfile(Long profile) {
    this.profile = profile;
  }

  /**
   * Gets userid.
   *
   * @return user id
   */
  public Long getUserid() {
    return userid;
  }

  /**
   * Sets userid.
   *
   * @param userid the userid
   */
  public void setUserid(Long userid) {
    this.userid = userid;
  }
}
