/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.exception;

/**
 * @author apierre
 */
public class CertificatePlatinesException extends ApiException {

  /** exception code. */
  public static final String EXCEPTION_CODE = "API_003";

  /** Default serial identifier. */
  private static final long serialVersionUID = 1L;

  /**
   * The constructor {@link #CertificatePlatinesException()} instantiates a new {@link
   * CertificatePlatinesException}. It only calls the corresponding parent constructor.
   */
  public CertificatePlatinesException() {
    super(EXCEPTION_CODE);
  }

  /**
   * The constructor {@link #CertificatePlatinesException(String)} instantiates a new {@link
   * CertificatePlatinesException}. It only calls the corresponding parent constructor.
   *
   * @param message : Message explaining why the exception occurred
   */
  public CertificatePlatinesException(String message) {
    super(EXCEPTION_CODE, message);
  }

  /**
   * The constructor {@link #CertificatePlatinesException(Throwable)} instantiates a new {@link
   * CertificatePlatinesException}. It only calls the corresponding parent constructor.
   *
   * @param cause : Exception cause
   */
  public CertificatePlatinesException(Throwable cause) {
    super(cause, EXCEPTION_CODE);
  }

  /**
   * The constructor {@link #CertificatePlatinesException(Throwable, String)} instantiates a new
   * {@link CertificatePlatinesException}. It only calls the corresponding parent constructor.
   *
   * @param cause : Exception cause
   * @param message : Message explaining why the exception occurred
   */
  public CertificatePlatinesException(Throwable cause, String message) {
    super(cause, EXCEPTION_CODE, message);
  }
}
