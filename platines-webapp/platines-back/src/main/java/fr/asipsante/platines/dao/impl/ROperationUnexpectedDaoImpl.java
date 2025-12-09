/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.dao.impl;

import fr.asipsante.platines.dao.IROperationUnexpectedDao;
import fr.asipsante.platines.entity.ROperationUnexpected;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 * @author aboittiaux
 */
@Repository(value = "rOperationUnexpected")
public class ROperationUnexpectedDaoImpl extends GenericDaoImpl<ROperationUnexpected, Long>
    implements IROperationUnexpectedDao {

  /** {@inheritDoc} */
  @Override
  public void persist(ROperationUnexpected rOperationUnexpected) {
    entityManager.persist(rOperationUnexpected);
  }

  @Override
  public List<ROperationUnexpected> getROperationsBySession(Long idSession) {
    return (List<ROperationUnexpected>)
        entityManager
            .createQuery(
                "SELECT op from ROperationUnexpected op where op.testSession.id = :idSession")
            .setParameter("idSession", idSession)
            .getResultList();
  }
}
