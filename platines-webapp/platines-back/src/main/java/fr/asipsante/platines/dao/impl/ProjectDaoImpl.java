/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.dao.impl;

import fr.asipsante.platines.dao.IProjectDao;
import fr.asipsante.platines.entity.Project;
import fr.asipsante.platines.entity.Version;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;

/**
 * @author aboittiaux
 */
@Repository(value = "projectDao")
public class ProjectDaoImpl extends GenericDaoImpl<Project, Long> implements IProjectDao {

  @Override
  @PostFilter(
      "(hasPermission(filterObject, 'READ') and hasAccess(filterObject)) or"
          + " isManaged(filterObject)")
  public List<Project> getProjectsByVersion(Long idVersion) {
    final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    final CriteriaQuery<Project> query = criteriaBuilder.createQuery(Project.class);
    final Root<Version> root = query.from(Version.class);
    final Join<Version, Project> projects = root.join("projects");
    query.select(projects).where(criteriaBuilder.equal(root.get("id"), idVersion));
    return entityManager.createQuery(query).getResultList();
  }

  @Override
  @PostAuthorize(
      "(hasPermission(returnObject, 'READ') and hasAccess(returnObject)) or"
          + " isManaged(returnObject)")
  public Project getById(Long id) {
    return entityManager.find(Project.class, id);
  }

  /** {@inheritDoc} */
  @Override
  @PreAuthorize("hasPermission(#project, 'DELETE') or isManaged(#project)")
  public void delete(Project project) {
    entityManager.remove(entityManager.find(Project.class, project.getId()));
  }

  /** {@inheritDoc} */
  @Override
  @PostFilter(
      "(hasPermission(filterObject, 'READ') and hasAccess(filterObject)) or"
          + " isManaged(filterObject)")
  public List<Project> getAll() {
    final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
    final CriteriaQuery<Project> query = builder.createQuery(Project.class);
    query.select(query.from(Project.class));
    return entityManager.createQuery(query).getResultList();
  }

  @Override
  @PostFilter(
      "(hasPermission(filterObject, 'READ') and hasAccess(filterObject)) or"
          + " isManaged(filterObject)")
  public List<Project> getProjectByFileName(String definitionName) {
    final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
    final CriteriaQuery<Project> query = builder.createQuery(Project.class);
    final Root<Project> project = query.from(Project.class);
    query.select(project).where(builder.equal(project.get("fileName"), definitionName));
    return entityManager.createQuery(query).getResultList();
  }
}
