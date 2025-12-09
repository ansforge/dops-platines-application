/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.dao;

import fr.asipsante.platines.entity.FunctionalData;

/**
 * Interface provides methods required to link {@link FunctionalData} entity to a data source.
 *
 * @author apierre
 */
public interface IFunctionalDataDao extends IGenericDao<FunctionalData, Long> {

  /**
   * Get the application properties.
   *
   * @return application properties
   */
  FunctionalData getProperties();

  /**
   * Saves functional data in database.
   *
   * @param entity, functional data to persist
   */
  void persist(FunctionalData entity);

  /**
   * Persists functional data and returns it.
   *
   * @param entity, functional data to save
   * @return functional data
   */
  FunctionalData persistAndGet(FunctionalData entity);
}
