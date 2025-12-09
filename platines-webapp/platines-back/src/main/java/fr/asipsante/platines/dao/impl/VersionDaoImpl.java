/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.dao.impl;

import fr.asipsante.platines.dao.IVersionDao;
import fr.asipsante.platines.entity.Project;
import fr.asipsante.platines.entity.Version;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;

/**
 * @author apierre
 */
@Repository(value = "versionDao")
public class VersionDaoImpl extends GenericDaoImpl<Version, Long> implements IVersionDao {

  /** Service. */
  public static final String SERVICE = "service";

  /** {@inheritDoc} */
  @Override
  @PostFilter(
      "(hasPermission(filterObject, 'READ') and hasAccess(filterObject)) or"
          + " isManaged(filterObject)")
  public List<Version> getVersionsByFamilyId(Long id) {
    final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
    final CriteriaQuery<Version> query = builder.createQuery(Version.class);
    final Root<Version> root = query.from(Version.class);
    query.select(root).where(builder.equal(root.get(SERVICE).get("theme").get("id"), id));
    return entityManager.createQuery(query).getResultList();
  }

  @Override
  @PostFilter(
      "(hasPermission(filterObject, 'READ') and hasAccess(filterObject)) or"
          + " isManaged(filterObject)")
  public List<Version> getVersionsByUser() {
    final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
    final CriteriaQuery<Version> query = builder.createQuery(Version.class);
    query.from(Version.class);
    return entityManager.createQuery(query).getResultList();
  }

  @Override
  @PostFilter(
      "(hasPermission(filterObject, 'READ') and hasAccess(filterObject)) or"
          + " isManaged(filterObject)")
  public List<Version> getVersionByProject(Long idProject) {
    final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
    final CriteriaQuery<Version> query = builder.createQuery(Version.class);
    final Root<Project> root = query.from(Project.class);
    final Join<Project, Version> versions = root.join("versions");
    query.select(versions).where(builder.equal(root.get("id"), idProject));
    return entityManager.createQuery(query).getResultList();
  }

  @Override
  @PostFilter(
      "(hasPermission(filterObject, 'READ') and hasAccess(filterObject)) or"
          + " isManaged(filterObject)")
  public List<Version> getVersionsBySystemId(Long idService) {
    final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
    final CriteriaQuery<Version> query = builder.createQuery(Version.class);
    final Root<Version> root = query.from(Version.class);
    query.select(root).where(builder.equal(root.get(SERVICE).get("id"), idService));
    return entityManager.createQuery(query).getResultList();
  }

  /** {@inheritDoc} */
  @Override
  @PreAuthorize("hasPermission(#version, 'WRITE') or isManaged(#version)")
  public void update(Version version) {
    entityManager.merge(version);
  }
}
