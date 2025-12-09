/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.exception;

/**
 * @author aboittiaux
 */
public class ServiceException extends ApiException {

  /** exception code. */
  public static final String EXCEPTION_CODE = "API_005";

  /** Default serial identifier. */
  private static final long serialVersionUID = 1L;

  /**
   * The constructor {@link #ServiceException()} instantiates a new {@link ServiceException}. It
   * only calls the corresponding parent constructor.
   */
  public ServiceException() {
    super(EXCEPTION_CODE);
  }

  /**
   * The constructor {@link #ServiceException(String)} instantiates a new {@link ServiceException}.
   * It only calls the corresponding parent constructor.
   *
   * @param message : Message explaining why the exception occurred
   */
  public ServiceException(String message) {
    super(EXCEPTION_CODE, message);
  }

  /**
   * The constructor {@link #ServiceException(Throwable)} instantiates a new {@link
   * ServiceException}. It only calls the corresponding parent constructor.
   *
   * @param cause : Exception cause
   */
  public ServiceException(Throwable cause) {
    super(cause, EXCEPTION_CODE);
  }

  /**
   * The constructor {@link #ServiceException(Throwable, String)} instantiates a new {@link
   * ServiceException}. It only calls the corresponding parent constructor.
   *
   * @param cause : Exception cause
   * @param message : Message explaining why the exception occurred
   */
  public ServiceException(Throwable cause, String message) {
    super(cause, EXCEPTION_CODE, message);
  }
}
