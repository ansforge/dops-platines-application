/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.entity.enums;

/**
 * @author aboittiaux
 */
public enum KeyPreference {

  /** text home page. */
  TEXT_HOME_PAGE("textHomePage"),
  /** token duration. */
  TOKEN_DURATION("token_duration"),
  /** blocked duration. */
  BLOCKED_ACCOUNT_DURATION("blocked_account_duration"),
  /** try authentication. */
  LIMIT_TRY_AUTHENTICATION("limit_try_authentication"),
  /** provisionning timeout. */
  PROVISIONNING_TIMEOUT("provisionning_timeout"),
  /** email notification. */
  LIST_EMAIL_NOTIFICATION("list_email_notification");

  /** Value. */
  private String value;

  /**
   * @param value
   */
  private KeyPreference(String value) {
    this.value = value;
  }

  /**
   * @return the value
   */
  public String getValue() {
    return value;
  }
}
