/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.ans.platines.vihf.exception;

public class GenericVihfException extends RuntimeException {
  public GenericVihfException(String message) {
    super(message);
  }

  public GenericVihfException(String message, Throwable cause) {
    super(message, cause);
  }
}
