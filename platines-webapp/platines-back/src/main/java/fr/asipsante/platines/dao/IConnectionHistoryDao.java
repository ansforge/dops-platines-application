/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.dao;

import fr.asipsante.platines.entity.ConnectionHistory;

/**
 * Interface provides methods required to link {@link ConnectionHistory} entity to a data source.
 *
 * @author apierre
 */
public interface IConnectionHistoryDao extends IGenericDao<ConnectionHistory, Long> {

  /**
   * The method saveConnectionHistory(ConnectionHistory) saves persist connectionHistory to the
   * database.
   *
   * @param connectionHistory ConnectionHistory to save in the database.
   */
  void saveConnectionHistory(ConnectionHistory connectionHistory);
}
