/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.exception;

/**
 * Thrown when API interaction with nomad fails (as opposed to job handling algorithm, when we throw
 * {@link JobException}.
 *
 * @author aboittiaux
 */
public class ServiceNomadException extends ApiException {

  /** exception code. */
  public static final String EXCEPTION_CODE = "API_008";

  /** uuid. */
  private static final long serialVersionUID = 3809480662622572636L;

  /**
   * @param cause Exception cause
   * @param errorCode error Code
   */
  public ServiceNomadException(Throwable cause, String errorCode) {
    super(cause, EXCEPTION_CODE, errorCode);
  }
}
