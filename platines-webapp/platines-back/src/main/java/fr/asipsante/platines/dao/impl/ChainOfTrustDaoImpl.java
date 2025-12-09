/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.dao.impl;

import fr.asipsante.platines.dao.IChainOfTrustDao;
import fr.asipsante.platines.entity.ChainOfTrust;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;

/**
 * @author aboittiaux
 */
@Repository(value = "chainOfTrustDao")
public class ChainOfTrustDaoImpl extends GenericDaoImpl<ChainOfTrust, Long>
    implements IChainOfTrustDao {

  @Override
  @PostFilter("hasPermission(filterObject, 'READ')")
  public List<ChainOfTrust> getChainByUser(Long idUser) {
    final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    final CriteriaQuery<ChainOfTrust> criteriaQuery = cb.createQuery(ChainOfTrust.class);
    final Root<ChainOfTrust> root = criteriaQuery.from(ChainOfTrust.class);
    criteriaQuery.select(root).where(cb.equal(root.get("user").get("id"), idUser));
    return entityManager.createQuery(criteriaQuery).getResultList();
  }

  /** {@inheritDoc} */
  @Override
  @PostFilter("hasPermission(filterObject, 'READ') or isManaged(filterObject)")
  public List<ChainOfTrust> getAll() {
    final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
    final CriteriaQuery<ChainOfTrust> query =
        (CriteriaQuery<ChainOfTrust>) builder.createQuery(clazz);
    query.select(query.from(clazz));
    return entityManager.createQuery(query).getResultList();
  }

  /** {@inheritDoc} */
  @Override
  @PreAuthorize("hasPermission(#chainOfTrust, 'WRITE') or isManaged(#chainOfTrust)")
  public void update(ChainOfTrust chainOfTrust) {
    entityManager.merge(chainOfTrust);
  }
}
