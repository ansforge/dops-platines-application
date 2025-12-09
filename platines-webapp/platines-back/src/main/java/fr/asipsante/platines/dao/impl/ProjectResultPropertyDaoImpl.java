/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.dao.impl;

import fr.asipsante.platines.dao.IProjectResultPropertyDao;
import fr.asipsante.platines.entity.ProjectResultProperty;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.springframework.stereotype.Repository;

/**
 * @author apierre
 */
@Repository(value = "projectResultPropertyDao")
public class ProjectResultPropertyDaoImpl extends GenericDaoImpl<ProjectResultProperty, Long>
    implements IProjectResultPropertyDao {

  @Override
  public ProjectResultProperty saveAndGet(ProjectResultProperty entity) {
    final ProjectResultProperty property = new ProjectResultProperty();
    property.setDescription(entity.getDescription());
    property.setKey(entity.getKey());
    property.setValue(entity.getValue());
    property.setPropertyType(entity.getPropertyType());
    property.setProjectResult(entity.getProjectResult());
    entityManager.merge(property);
    final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
    final CriteriaQuery<Long> query = builder.createQuery(Long.class);
    final Root<ProjectResultProperty> root = query.from(ProjectResultProperty.class);
    query.select(builder.max(root.get("id")));
    final Long id = entityManager.createQuery(query).getSingleResult();
    return getById(id);
  }
}
