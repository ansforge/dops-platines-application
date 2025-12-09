/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.dao;

import fr.asipsante.platines.entity.SessionDuration;
import java.util.List;

/**
 * Interface provides methods required to link {@link SessionDuration} entity to a data source.
 *
 * @author apierre
 */
public interface ISessionDurationDao extends IGenericDao<SessionDuration, Long> {

  /**
   * Gets all session duration.
   *
   * @return all session durations
   */
  List<SessionDuration> getAllDurations();
}
