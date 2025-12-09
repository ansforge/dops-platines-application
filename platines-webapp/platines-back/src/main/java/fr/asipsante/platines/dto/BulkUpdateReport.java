/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Rapport de mise à jour.
 *
 * @author edegenetais
 */
public class BulkUpdateReport {
  /**
   * The update outcome for a given line. {@link #WARNING} or {@link #REJECTED} status is explained
   * by the matching status message.
   *
   * @see {@link BulkUpdateReport#ReportLine#statusMessage}
   */
  public static enum UpdateStatus {
    /** The update was done without any problem. */
    OK,
    /**
     * The update was done, but with a warning.
     *
     * @see {@link BulkUpdateReport#ReportLine#statusMessage}
     */
    WARNING,
    /**
     * The update was rejected.
     *
     * @see {@link BulkUpdateReport#ReportLine#statusMessage}
     */
    REJECTED
  };

  /** Ligne de rapport de mise à jour en masse. */
  public static record ReportLine(
      String filename,
      String previousName,
      String newName,
      Date lastUpdateDate,
      UpdateStatus status,
      String statusMessage) {
    public ReportLine {
      Objects.requireNonNull(filename, "the filename attribute is mandatory");
      Objects.requireNonNull(status, "the status is mandatory");
      if ((status == UpdateStatus.REJECTED || status == UpdateStatus.WARNING)
          && (statusMessage == null || statusMessage.isBlank())) {
        throw new IllegalArgumentException(
            "Non-blank status message mandatory for " + status + " status");
      }
    }
  }

  /** Identifiant attribué à l'archive. */
  public final String archiveId;

  /** Thème filtre. */
  public final long themeId;

  /** Données du rapport. */
  private final List<ReportLine> reportLines;

  public BulkUpdateReport(String archiveId, List<ReportLine> reportLines, final long themeId) {
    this.archiveId = archiveId;
    this.reportLines = new ArrayList<>(Objects.requireNonNullElse(reportLines, List.of()));
    this.themeId = themeId;
  }

  public List<ReportLine> getReportLines() {
    return Collections.unmodifiableList(reportLines);
  }
}
