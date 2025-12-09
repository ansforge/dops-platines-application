/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.dto;

/**
 * @author aboittiaux
 */
public class LoginDto {

  /** The token. */
  private String token;

  /** The user. */
  private ProfileDto profile;

  /** Constructeur. */
  public LoginDto() {
    super();
  }

  /**
   * @return the token
   */
  public String getToken() {
    return token;
  }

  /**
   * @param token the token to set
   */
  public void setToken(String token) {
    this.token = token;
  }

  /**
   * @return the profile
   */
  public ProfileDto getProfile() {
    return profile;
  }

  /**
   * @param profile the profile to set
   */
  public void setProfile(ProfileDto profile) {
    this.profile = profile;
  }
}
