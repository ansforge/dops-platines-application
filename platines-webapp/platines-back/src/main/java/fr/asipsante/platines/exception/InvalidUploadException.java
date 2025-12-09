/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.exception;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Exception lancée si une ressrouce uplodaée dest invalide.
 *
 * @author edegenetais
 */
public class InvalidUploadException extends ApiException {
  private static final String INVALID_UPLOAD_ERROR_CODE = "Invalid upload";

  private final List<String> anomalies;

  public InvalidUploadException(Throwable cause, String message) {
    super(cause, INVALID_UPLOAD_ERROR_CODE, message);
    anomalies = Collections.emptyList();
  }

  public InvalidUploadException(final List<String> anomalies, final String message) {
    super(
        INVALID_UPLOAD_ERROR_CODE,
        message + "\n" + Objects.requireNonNullElse(anomalies, Collections.emptyList()));
    this.anomalies = anomalies;
  }
}
