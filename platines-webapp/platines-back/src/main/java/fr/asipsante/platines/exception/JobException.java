/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.exception;

/**
 * Let's suppose this is what we throw when jobhandling fails, as opposed to what we throw when
 * nomad calls fail, in which cas we throw {@link ServiceNomadException}
 *
 * @author aboittiaux
 */
public class JobException extends ApiException {

  /** uuid. */
  private static final long serialVersionUID = 1317713964436176270L;

  /**
   * @param msg, the error message
   */
  public JobException(String msg) {
    super(msg);
  }

  /**
   * @param cause, the error cause
   * @param errorCode, the error code
   */
  public JobException(Throwable cause, String errorCode) {
    super(cause, errorCode);
  }
}
