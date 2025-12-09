/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.model.bulkupdate;

/**
 * Exception used to report unpexpected chekd exceptions during bulk archive processing.
 *
 * @author edegenetais
 */
public class BulkArchiveException extends RuntimeException {

  public BulkArchiveException(String message, Throwable cause) {
    super(message, cause);
  }
}
