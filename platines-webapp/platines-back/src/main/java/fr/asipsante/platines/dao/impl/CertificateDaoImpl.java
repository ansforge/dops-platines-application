/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.dao.impl;

import fr.asipsante.platines.dao.ICertificateDao;
import fr.asipsante.platines.entity.Certificate;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.stereotype.Repository;

/**
 * @author aboittiaux
 */
@Repository(value = "certificateDao")
public class CertificateDaoImpl extends GenericDaoImpl<Certificate, Long>
    implements ICertificateDao {

  @Override
  @PostFilter("hasPermission(filterObject, 'READ') or isManaged(filterObject)")
  public List<Certificate> getAllByIdChain(Long idChain) {
    final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    final CriteriaQuery<Certificate> criteriaQuery = criteriaBuilder.createQuery(Certificate.class);
    final Root<Certificate> root = criteriaQuery.from(Certificate.class);
    criteriaQuery
        .select(root)
        .where(criteriaBuilder.equal(root.get("chainOfTrust").get("id"), idChain));
    return entityManager.createQuery(criteriaQuery).getResultList();
  }
}
