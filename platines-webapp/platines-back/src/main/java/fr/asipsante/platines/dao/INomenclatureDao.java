/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.dao;

import fr.asipsante.platines.entity.Nomenclature;
import java.util.Date;

/**
 * Interface provides methods required to link {@link Nomenclature} entity to a data source.
 *
 * @author apierre
 */
public interface INomenclatureDao extends IGenericDao<Nomenclature, Long> {

  /**
   * Gets the date of the last nomenclature update.
   *
   * @return the last update date
   */
  Date getLastUpdateNomenclature();

  /**
   * Gets the last nomenclature save in database.
   *
   * @return the last nomenclature
   */
  Nomenclature getLastNomenclature();
}
