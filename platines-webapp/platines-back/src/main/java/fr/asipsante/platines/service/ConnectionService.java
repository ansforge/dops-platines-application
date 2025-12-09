/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.service;

/**
 * The Connection service.
 *
 * @author apierre
 */
public interface ConnectionService {

  /**
   * Connects the user to the application.
   *
   * @param email, the user email
   * @param password, the user password
   * @return the user token
   */
  String authenticate(String email, String password);

  /**
   * Verify that the token is valid.
   *
   * @param token, the token to verify
   * @return the user token
   */
  String validateToken(String token);

  /**
   * Sends an email to the user for change its password.
   *
   * @param email, the mail address
   */
  void forgotPassword(String email);

  /**
   * Changes the user password.
   *
   * @param token, user token
   * @param password, the new password
   */
  void resetPassword(String token, String password);
}
