/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.dao.impl;

import fr.asipsante.platines.dao.IThemeDao;
import fr.asipsante.platines.entity.Theme;
import fr.asipsante.platines.entity.enums.Role;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.stereotype.Repository;

/**
 * Implementation of {@link IThemeDao}.
 *
 * @author apierre
 */
@Repository()
public class ThemeDaoImpl extends GenericDaoImpl<Theme, Long> implements IThemeDao {

  /** {@inheritDoc} */
  @Override
  @PostFilter("hasAccess(filterObject) or isManaged(filterObject)")
  public List<Theme> getAll() {
    final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
    final CriteriaQuery<Theme> query = builder.createQuery(Theme.class);
    query.select(query.from(Theme.class));
    return entityManager.createQuery(query).getResultList();
  }

  @Override
  @PostAuthorize("hasAccess(returnObject) or isManaged(returnObject)")
  public Theme safeFetchById(Long id) {
    return entityManager.find(Theme.class, id);
  }

  @Override
  public Map<Theme, Set<Role>> getCoveredRoleMapping() {
    List<Theme> families = getAll();
    Map<Theme, Set<Role>> coveredRoleMapping = new HashMap<>(families.size());
    families.forEach(
        f -> {
          String queryString =
              """
                            select distinct p.role from Project p
                            inner join p.versions as versions
                            inner join versions.service as service
                            where service.theme.id = ?0
                            """;
          TypedQuery<Role> query = entityManager.createQuery(queryString, Role.class);
          query.setParameter(0, f.getId());
          List<Role> coverage = query.getResultList();
          coveredRoleMapping.put(f, new HashSet<>(coverage));
        });
    return coveredRoleMapping;
  }
}
