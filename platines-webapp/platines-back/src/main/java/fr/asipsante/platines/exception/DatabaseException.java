/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.exception;

/**
 * The {@link DatabaseException} exception should be thrown when an exception occurs while trying to
 * connect to a data source (database or other).
 *
 * @author apierre
 */
public class DatabaseException extends ApiException {

  /** The Constant EXCEPTION_CODE. */
  public static final String EXCEPTION_CODE = "API_002";

  /** {@link DatabaseException}'s default serial identifier. */
  private static final long serialVersionUID = -6942586470517058114L;

  /**
   * The constructor {@link #DatabaseException()} instantiates a new {@link DatabaseException}. It
   * only calls the corresponding parent constructor.
   */
  public DatabaseException() {
    super(EXCEPTION_CODE);
  }

  /**
   * The constructor {@link #DatabaseException(String)} instantiates a new {@link
   * DatabaseException}. It only calls the corresponding parent constructor.
   *
   * @param message : Message explaining why the exception occurred
   */
  public DatabaseException(String message) {
    super(EXCEPTION_CODE, message);
  }

  /**
   * The constructor {@link #DatabaseException(Throwable)} instantiates a new {@link
   * DatabaseException}. It only calls the corresponding parent constructor.
   *
   * @param cause : Exception cause
   */
  public DatabaseException(Throwable cause) {
    super(cause, EXCEPTION_CODE);
  }

  /**
   * The constructor {@link #DatabaseException(Throwable, String)} instantiates a new {@link
   * DatabaseException}. It only calls the corresponding parent constructor.
   *
   * @param cause : Exception cause
   * @param message : Message explaining why the exception occurred
   */
  public DatabaseException(Throwable cause, String message) {
    super(cause, EXCEPTION_CODE, message);
  }
}
