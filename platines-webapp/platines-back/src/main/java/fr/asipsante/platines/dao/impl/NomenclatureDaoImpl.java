/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.dao.impl;

import fr.asipsante.platines.dao.INomenclatureDao;
import fr.asipsante.platines.entity.Nomenclature;
import fr.asipsante.platines.exception.DatabaseException;
import java.util.Date;
import javax.persistence.NonUniqueResultException;
import org.springframework.stereotype.Repository;

/**
 * Implementation of {@link INomenclatureDao}.
 *
 * @author apierre
 */
@Repository(value = "nomenclatureDao")
public class NomenclatureDaoImpl extends GenericDaoImpl<Nomenclature, Long>
    implements INomenclatureDao {

  @Override
  public Date getLastUpdateNomenclature() {
    // Requete SQL
    final String request = "SELECT MAX(n.dateUpload) from Nomenclature n";
    // Date de retour
    Date lastUpdate;
    try {
      lastUpdate = (Date) entityManager.createQuery(request).getSingleResult();
    } catch (final NonUniqueResultException e) {
      throw new DatabaseException(e, "pas de nomenclature");
    }
    return lastUpdate;
  }

  @Override
  public Nomenclature getLastNomenclature() {
    final String request =
        "SELECT n FROM Nomenclature n "
            + "WHERE n.dateUpload = (SELECT MAX(n.dateUpload) from Nomenclature n)";
    Nomenclature nomenclature;
    try {
      nomenclature = (Nomenclature) entityManager.createQuery(request).getSingleResult();
    } catch (final NonUniqueResultException e) {
      throw new DatabaseException(e, "pas de nomenclature");
    }
    return nomenclature;
  }
}
