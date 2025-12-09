/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.dao.impl;

import fr.asipsante.platines.dao.ISimulatedServiceDao;
import fr.asipsante.platines.entity.SimulatedService;
import fr.asipsante.platines.entity.enums.Role;
import java.util.List;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.stereotype.Repository;

/**
 * Implementation of {@link ISimulatedServiceDao}.
 *
 * @author apierre
 */
@Repository(value = "simulatedServiceDao")
public class SimulatedServiceDao extends GenericDaoImpl<SimulatedService, Long>
    implements ISimulatedServiceDao {

  /** {@inheritDoc} */
  @Override
  @PostFilter("hasAccess(filterObject) or isManaged(filterObject)")
  public List<SimulatedService> getSimulatedServicesForRoleInFamily(Long familyId, Role role) {
    String queryString =
        """
                     select distinct s from Project p
                     inner join p.versions as v
                     inner join v.service as s
                     where p.role = :role
                     and s.theme.id = :familyId
                     """;
    TypedQuery query = entityManager.createQuery(queryString, SimulatedService.class);
    query.setParameter("role", role);
    query.setParameter("familyId", familyId);
    return query.getResultList();
  }

  /** {@inheritDoc} */
  @Override
  @PostFilter("hasAccess(filterObject) or isManaged(filterObject)")
  public List<SimulatedService> getSimulatedServicesByFamilyId(Long familyId) {
    final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
    final CriteriaQuery<SimulatedService> query = builder.createQuery(SimulatedService.class);
    final Root<SimulatedService> root = query.from(SimulatedService.class);
    query.select(root).where(builder.equal(root.get("theme").get("id"), familyId));

    return entityManager.createQuery(query).getResultList();
  }

  /** {@inheritDoc} */
  @Override
  @PostFilter("hasAccess(filterObject) or isManaged(filterObject)")
  public List<SimulatedService> getAll() {
    final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
    final CriteriaQuery<SimulatedService> query = builder.createQuery(SimulatedService.class);
    query.select(query.from(SimulatedService.class));
    return entityManager.createQuery(query).getResultList();
  }
}
