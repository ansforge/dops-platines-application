/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.dao;

import fr.asipsante.platines.entity.ProjectResult;

/**
 * Interface provides methods required to link {@link ProjectResult} entity to a data source.
 *
 * @author apierre
 */
public interface IProjectResultDao extends IGenericDao<ProjectResult, Long> {

  /**
   * Updates a project result without the token.
   *
   * @param projectResult, the project result to update
   */
  void updateProjectResultWithoutToken(ProjectResult projectResult);

  /**
   * Gets a project result by its id without token.
   *
   * @param id, project result id
   * @return the project result
   */
  ProjectResult getByIdWithoutToken(Long id);
}
