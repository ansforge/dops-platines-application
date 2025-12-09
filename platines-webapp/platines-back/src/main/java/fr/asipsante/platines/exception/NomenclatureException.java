/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.exception;

/**
 * @author apierre
 */
public class NomenclatureException extends ApiException {

  /** exception code. */
  public static final String EXCEPTION_CODE = "API_004";

  /** Default serial identifier. */
  private static final long serialVersionUID = 1L;

  /**
   * The constructor {@link #NomenclatureException()} instantiates a new {@link
   * NomenclatureException}. It only calls the corresponding parent constructor.
   */
  public NomenclatureException() {
    super(EXCEPTION_CODE);
  }

  /**
   * The constructor {@link #NomenclatureException(String)} instantiates a new {@link
   * NomenclatureException}. It only calls the corresponding parent constructor.
   *
   * @param message : Message explaining why the exception occurred
   */
  public NomenclatureException(String message) {
    super(EXCEPTION_CODE, message);
  }

  /**
   * The constructor {@link #NomenclatureException(Throwable)} instantiates a new {@link
   * NomenclatureException}. It only calls the corresponding parent constructor.
   *
   * @param cause : Exception cause
   */
  public NomenclatureException(Throwable cause) {
    super(cause, EXCEPTION_CODE);
  }

  /**
   * The constructor {@link #NomenclatureException(Throwable, String)} instantiates a new {@link
   * NomenclatureException}. It only calls the corresponding parent constructor.
   *
   * @param cause : Exception cause
   * @param message : Message explaining why the exception occurred
   */
  public NomenclatureException(Throwable cause, String message) {
    super(cause, EXCEPTION_CODE, message);
  }
}
