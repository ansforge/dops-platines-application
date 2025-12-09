/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.dao;

import fr.asipsante.platines.entity.ROperationUnexpected;
import java.util.List;

/**
 * Interface provides methods required to link {@link ROperationUnexpected} entity to a data source.
 *
 * @author aboittiaux
 */
public interface IROperationUnexpectedDao extends IGenericDao<ROperationUnexpected, Long> {

  /**
   * Saves an unexpected operation.
   *
   * @param rOperationUnexpected, the operation to save
   */
  void persist(ROperationUnexpected rOperationUnexpected);

  /**
   * Gets all unexpected operation for a session.
   *
   * @param idSession, the session id
   * @return all unexpected operation
   */
  List<ROperationUnexpected> getROperationsBySession(Long idSession);
}
