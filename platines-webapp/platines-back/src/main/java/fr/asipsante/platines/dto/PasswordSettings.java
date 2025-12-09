/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.dto;

/**
 * @author aboittiaux
 */
public class PasswordSettings {

  /** last password. */
  private String lastPassword;

  /** new password. */
  private String newPassword;

  /**
   * @return the lastPassword
   */
  public String getLastPassword() {
    return lastPassword;
  }

  /**
   * @param lastPassword the lastPassword to set
   */
  public void setLastPassword(String lastPassword) {
    this.lastPassword = lastPassword;
  }

  /**
   * @return the newPassword
   */
  public String getNewPassword() {
    return newPassword;
  }

  /**
   * @param newPassword the newPassword to set
   */
  public void setNewPassword(String newPassword) {
    this.newPassword = newPassword;
  }
}
