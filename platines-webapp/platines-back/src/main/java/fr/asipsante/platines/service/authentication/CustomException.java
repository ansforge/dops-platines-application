/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.service.authentication;

import org.springframework.http.HttpStatus;

/**
 * @author apierre
 */
public class CustomException extends RuntimeException {

  /** uuid. */
  private static final long serialVersionUID = 1L;

  /** message. */
  private final String message;

  /** http status. */
  private final HttpStatus httpStatus;

  /**
   * Constructeur.
   *
   * @param message message
   * @param httpStatus httpStatus
   */
  public CustomException(String message, HttpStatus httpStatus) {
    this.message = message;
    this.httpStatus = httpStatus;
  }

  /**
   * @return message.
   */
  @Override
  public String getMessage() {
    return message;
  }

  /**
   * @return httpStatus
   */
  public HttpStatus getHttpStatus() {
    return httpStatus;
  }
}
