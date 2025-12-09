/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.dao.impl;

import fr.asipsante.platines.dao.IFunctionalDataDao;
import fr.asipsante.platines.entity.FunctionalData;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.springframework.stereotype.Repository;

/**
 * @author apierre
 */
@Repository(value = "functionalDataDao")
public class FunctionalDataDaoImpl extends GenericDaoImpl<FunctionalData, Long>
    implements IFunctionalDataDao {

  @Override
  public FunctionalData getProperties() {
    final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
    final CriteriaQuery<FunctionalData> query = builder.createQuery(FunctionalData.class);
    final Root<FunctionalData> root = query.from(FunctionalData.class);
    query.select(root);
    FunctionalData functionalData;
    try {
      functionalData = entityManager.createQuery(query).getSingleResult();
    } catch (final NoResultException e) {
      functionalData = null;
    }
    return functionalData;
  }

  @Override
  public void persist(FunctionalData entity) {
    if (entity.getId() != null) {
      this.update(entity);
    } else {
      this.persist(entity);
    }
  }

  /** {@inheritDoc} */
  @Override
  public FunctionalData persistAndGet(FunctionalData entity) {
    entityManager.persist(entity);
    final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
    final CriteriaQuery<Long> query = builder.createQuery(Long.class);
    final Root<FunctionalData> root = query.from(FunctionalData.class);
    query.select(builder.max(root.get("id")));
    final Long id = entityManager.createQuery(query).getSingleResult();
    return getById(id);
  }
}
