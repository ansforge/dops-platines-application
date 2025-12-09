/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.dao.impl;

import fr.asipsante.platines.dao.IResourceDao;
import fr.asipsante.platines.entity.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;

/**
 * Implementation of {@link IResourceDao}.
 *
 * @author cnader
 */
@Repository(value = "resourceDao")
public class ResourceDaoImpl extends GenericDaoImpl<Resource, Long> implements IResourceDao {

  /** entity manager. */
  @PersistenceContext protected EntityManager entityManager;

  @Override
  public List<Resource> getResourcesByAssociation(Long associationId) {
    final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    final CriteriaQuery<Resource> query = criteriaBuilder.createQuery(Resource.class);
    final Root<Resource> root = query.from(Resource.class);
    query
        .select(root)
        .where(criteriaBuilder.equal(root.get("association").get("id"), associationId));
    return entityManager.createQuery(query).getResultList();
  }

  /** {@inheritDoc} */
  @Override
  @PreAuthorize("isManaged(#resource)")
  public void delete(Resource resource) {
    entityManager.remove(entityManager.find(Resource.class, resource.getId()));
  }

  /** {@inheritDoc} */
  @Override
  @PreAuthorize("isManaged(#resource)")
  public void update(Resource resource) {
    entityManager.merge(resource);
  }

  @Override
  public Resource getLastNomenclature(long[] associationsIds) {

    final List<Resource> nomenclatures = new ArrayList<>();
    for (final Long id : associationsIds) {
      nomenclatures.addAll(
          entityManager
              .createQuery(
                  "SELECT n FROM Resource n WHERE n.association.id = :idResource AND n.fileType ="
                      + " 'NOMENCLATURE'")
              .setParameter("idResource", id)
              .getResultList());
    }
    if (!nomenclatures.isEmpty()) {
      return Collections.max(nomenclatures, Comparator.comparing(Resource::getDateUpload));
    }

    return null;
  }

  @Override
  public List<Resource> getResourcesByAssociations(long[] associationsIds) {

    final List<Resource> resources = new ArrayList<>();
    for (final Long id : associationsIds) {
      resources.addAll(
          entityManager
              .createQuery(
                  "SELECT n FROM Resource n WHERE n.association.id = :idResource AND n.fileType <>"
                      + " 'NOMENCLATURE'")
              .setParameter("idResource", id)
              .getResultList());
    }

    return resources;
  }
}
