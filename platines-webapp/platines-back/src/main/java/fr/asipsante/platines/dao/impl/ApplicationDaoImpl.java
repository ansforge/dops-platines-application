/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.dao.impl;

import fr.asipsante.platines.dao.IApplicationDao;
import fr.asipsante.platines.entity.Application;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.stereotype.Repository;

/**
 * @author aboittiaux
 */
@Repository(value = "applicationDao")
public class ApplicationDaoImpl extends GenericDaoImpl<Application, Long>
    implements IApplicationDao {

  @Override
  @PostFilter("hasPermission(filterObject, 'READ') or isManaged(filterObject)")
  public List<Application> getApplicationsByUser(Long id) {
    final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    final CriteriaQuery<Application> query = cb.createQuery(Application.class);
    final Root<Application> root = query.from(Application.class);
    query.select(root).where(cb.equal(root.get("user").get("id"), id));
    return entityManager.createQuery(query).getResultList();
  }

  @Override
  @PostFilter("hasPermission(filterObject, 'READ') or isManaged(filterObject)")
  public List<Application> getApplicationsByIdChain(Long idChain) {
    final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    final CriteriaQuery<Application> query = cb.createQuery(Application.class);
    final Root<Application> root = query.from(Application.class);
    query.select(root).where(cb.equal(root.get("chainOfTrust").get("id"), idChain));
    return entityManager.createQuery(query).getResultList();
  }

  /** {@inheritDoc} */
  @PostFilter("hasPermission(filterObject, 'READ') or isManaged(filterObject)")
  public List<Application> getAllFiltered() {
    final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
    final CriteriaQuery<Application> query =
        (CriteriaQuery<Application>) builder.createQuery(clazz);
    query.select(query.from(clazz));
    return entityManager.createQuery(query).getResultList();
  }
}
