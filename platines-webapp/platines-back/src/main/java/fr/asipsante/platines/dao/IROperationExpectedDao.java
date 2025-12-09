/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.dao;

import fr.asipsante.platines.entity.ROperationExpected;
import java.util.List;

/**
 * Interface provides methods required to link {@link ROperationExpected} entity to a data source.
 *
 * @author aboittiaux
 */
public interface IROperationExpectedDao extends IGenericDao<ROperationExpected, Long> {

  /**
   * Saves a expected operation.
   *
   * @param operationExpected, the operation to save.
   */
  void persist(ROperationExpected operationExpected);

  /**
   * Gets all expected operations for a session.
   *
   * @param idSession, the session id
   * @return list of expected operations.
   */
  List<ROperationExpected> getROperationExpectedBySession(Long idSession);

  /**
   * Gets an expected operation by its id.
   *
   * @param id, the operation id
   * @return the expected operation
   */
  ROperationExpected getROperationExpectedById(Long id);
}
