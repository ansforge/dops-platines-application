/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.dao.impl;

import fr.asipsante.platines.dao.IROperationExpectedDao;
import fr.asipsante.platines.entity.ROperationExpected;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 * @author aboittiaux
 */
@Repository(value = "rOperationExpected")
public class ROperationExpectedDaoImpl extends GenericDaoImpl<ROperationExpected, Long>
    implements IROperationExpectedDao {

  /** {@inheritDoc} */
  @Override
  public void persist(ROperationExpected caseResultOperation) {
    entityManager.persist(caseResultOperation);
  }

  @Override
  public List<ROperationExpected> getROperationExpectedBySession(Long idSession) {
    return (List<ROperationExpected>)
        entityManager
            .createQuery(
                "SELECT op FROM ROperationExpected op "
                    + "where op.testCaseResult.testSuite.projectResult.testSession.id = :idSession")
            .setParameter("idSession", idSession)
            .getResultList();
  }

  @Override
  public ROperationExpected getROperationExpectedById(Long id) {
    return entityManager.find(ROperationExpected.class, id);
  }
}
