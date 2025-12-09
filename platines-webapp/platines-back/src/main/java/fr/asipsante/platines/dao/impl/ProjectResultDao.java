/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.dao.impl;

import fr.asipsante.platines.dao.IProjectResultDao;
import fr.asipsante.platines.entity.ProjectResult;
import org.springframework.stereotype.Repository;

/**
 * @author apierre
 */
@Repository(value = "projectResultDao")
public class ProjectResultDao extends GenericDaoImpl<ProjectResult, Long>
    implements IProjectResultDao {

  @Override
  public void updateProjectResultWithoutToken(ProjectResult projectResult) {
    entityManager.merge(projectResult);
  }

  @Override
  public ProjectResult getByIdWithoutToken(Long id) {
    return entityManager.find(ProjectResult.class, id);
  }
}
