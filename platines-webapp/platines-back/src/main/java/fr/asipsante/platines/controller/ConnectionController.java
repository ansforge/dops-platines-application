/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.controller;

import fr.asipsante.platines.dto.LoginRequestDto;
import fr.asipsante.platines.exception.ServiceException;
import fr.asipsante.platines.service.ConnectionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author apierre
 */
@RequestMapping("/insecure/")
@RestController
public class ConnectionController {

  /** The connection service. */
  @Autowired ConnectionService connectionService;

  /** LOGGER. */
  private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionController.class);

  /**
   * Connects the user to the application.
   *
   * @param userLogin the user login and the user password
   * @return response
   */
  @PostMapping(value = "authent", produces = "application/json")
  public String login(@RequestBody final LoginRequestDto userLogin) {
    String response = null;
    try {
      response = connectionService.authenticate(userLogin.getLogin(), userLogin.getPassword());
    } catch (final ServiceException e) {
      LOGGER.error("Error while authenticating user : ", e);
    }
    return response;
  }

  /**
   * Validate token.
   *
   * @param validateToken token
   * @return token
   */
  @PostMapping(value = "validateToken", produces = "application/json")
  public String validateToken(@RequestBody final String validateToken) {
    return connectionService.validateToken(validateToken);
  }

  /**
   * Sends a mail for change the forgot password.
   *
   * @param mail mail address to send a mail
   */
  @PostMapping(value = "forgotPassword", produces = "application/json")
  public void forgotPassword(@RequestBody String mail) {
    connectionService.forgotPassword(mail);
  }

  /**
   * Changes the user password.
   *
   * @param userLogin object with the user login and the new password
   */
  @PostMapping(value = "resetPassword", produces = "application/json")
  public void reset(@RequestBody LoginRequestDto userLogin) {
    connectionService.resetPassword(userLogin.getToken(), userLogin.getPassword());
  }
}
