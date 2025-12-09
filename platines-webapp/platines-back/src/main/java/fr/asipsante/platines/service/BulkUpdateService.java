/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.service;

import fr.asipsante.platines.dto.BulkUpdateReport;
import fr.asipsante.platines.model.bulkupdate.BulkProjectUpdateArchive;
import java.io.IOException;

/**
 * Service dédié à la gestion des mises à jour de masse.
 *
 * @author ericdegenetais
 */
public interface BulkUpdateService {

  /**
   * Perform or simulate bulk update from a project bulk update archive.
   *
   * @param archive archive data.
   * @param themeId the themeId scope of the update.
   * @param effectiveUpdate this must be sety to true if this is a real update, to false if we are
   *     simulating the update.
   * @return the update report.
   * @throws IOException
   */
  BulkUpdateReport performUpdate(
      BulkProjectUpdateArchive archive, Long themeId, boolean effectiveUpdate) throws IOException;
}
