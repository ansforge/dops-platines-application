/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.exception;

/**
 * @author cnader
 */
public class ResourceException extends ApiException {

  /** exception code. */
  public static final String EXCEPTION_CODE = "API_004";

  /** Default serial identifier. */
  private static final long serialVersionUID = 1L;

  /**
   * The constructor {@link #ResourceException()} instantiates a new {@link ResourceException}. It
   * only calls the corresponding parent constructor.
   */
  public ResourceException() {
    super(EXCEPTION_CODE);
  }

  /**
   * The constructor {@link #ResourceException(String)} instantiates a new {@link
   * ResourceException}. It only calls the corresponding parent constructor.
   *
   * @param message : Message explaining why the exception occurred
   */
  public ResourceException(String message) {
    super(EXCEPTION_CODE, message);
  }

  /**
   * The constructor {@link #ResourceException(Throwable)} instantiates a new {@link
   * ResourceException}. It only calls the corresponding parent constructor.
   *
   * @param cause : Exception cause
   */
  public ResourceException(Throwable cause) {
    super(cause, EXCEPTION_CODE);
  }

  /**
   * The constructor {@link #ResourceException(Throwable, String)} instantiates a new {@link
   * ResourceException}. It only calls the corresponding parent constructor.
   *
   * @param cause : Exception cause
   * @param message : Message explaining why the exception occurred
   */
  public ResourceException(Throwable cause, String message) {
    super(cause, EXCEPTION_CODE, message);
  }
}
