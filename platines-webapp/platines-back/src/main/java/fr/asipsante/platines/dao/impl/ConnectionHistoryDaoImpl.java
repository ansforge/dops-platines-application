/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.dao.impl;

import fr.asipsante.platines.dao.IConnectionHistoryDao;
import fr.asipsante.platines.entity.ConnectionHistory;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

/**
 * @author aboittiaux
 */
@Repository(value = "connectionHistoryDao")
public class ConnectionHistoryDaoImpl extends GenericDaoImpl<ConnectionHistory, Long>
    implements IConnectionHistoryDao {

  /** entity manager. */
  @PersistenceContext protected EntityManager entityManager;

  /** {@inheritDoc} */
  @Override
  public void saveConnectionHistory(ConnectionHistory connectionHistory) {
    try {
      // Insert your entity by calling persist method
      entityManager.persist(connectionHistory);
    } catch (final EntityExistsException e) {
      // Entity already exists, merging instead
      entityManager.merge(connectionHistory);
    }
  }
}
