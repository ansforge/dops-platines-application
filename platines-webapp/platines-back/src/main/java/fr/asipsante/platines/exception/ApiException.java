/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.exception;

/**
 * The {@link ApiException} exception provides all the needed elements to create exceptions
 * answering the project exception management rules.
 *
 * <p>All the exceptions created in the project have to inherit this exception. This exception is
 * not directly instantiable.
 *
 * @author apierre
 */
public abstract class ApiException extends RuntimeException {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;

  /** The error code. */
  private final String errorCode;

  /** The error description. */
  private final String message;

  /**
   * Constructs a new exception with {@code null} as its detail message. The cause is not
   * initialized, and may subsequently be initialized by a call to {@link #initCause}.
   *
   * @param errorCode : Exception error code
   */
  public ApiException(String errorCode) {
    super();
    this.errorCode = errorCode;
    this.message = null;
  }

  /**
   * Constructs a new exception with {@code errorDescription} as its detail message. The cause is
   * not initialized, and may subsequently be initialized by a call to {@link #initCause}.
   *
   * @param errorCode : Exception error code
   * @param message : Exception description
   */
  public ApiException(String errorCode, String message) {
    super(message);
    this.errorCode = errorCode;
    this.message = message;
  }

  /**
   * Constructs a new exception with the specified cause and null message.
   *
   * @param cause : the cause, a <tt>null</tt> value is permitted, and indicates that the cause is
   *     not existent or unknown.
   * @param errorCode : Exception error code
   */
  public ApiException(Throwable cause, String errorCode) {
    super(cause);
    this.message = "";
    this.errorCode = errorCode;
  }

  /**
   * Constructs a new exception with the specified detail message and cause.
   *
   * @param cause : the cause, a <tt>null</tt> value is permitted, and indicates that the cause is
   *     not existent or unknown.
   * @param errorCode : Exception error code
   * @param message : Exception description
   */
  public ApiException(Throwable cause, String errorCode, String message) {
    super(message, cause);
    this.errorCode = errorCode;
    this.message = message;
  }

  /**
   * Gets the error code.
   *
   * @return the errorCode
   */
  public String getErrorCode() {
    return errorCode;
  }

  /**
   * Gets the error description.
   *
   * @return the errorDescription
   */
  public String getErrorDescription() {
    return message;
  }
}
