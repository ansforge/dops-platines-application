/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.dao;

import fr.asipsante.platines.entity.Version;
import java.util.List;

/**
 * Interface provides methods required to link {@link Version} entity to a data source.
 *
 * @author apierre
 */
public interface IVersionDao extends IGenericDao<Version, Long> {

  /**
   * Gets the list of versions for a family.
   *
   * @param id, the family id
   * @return the versions corresponding to the family id.
   */
  List<Version> getVersionsByFamilyId(Long id);

  /**
   * Gets the list of versions visible by a user.
   *
   * @return all the version visible
   */
  List<Version> getVersionsByUser();

  /**
   * Gets all the versions for a project.
   *
   * @param idProject, the project id
   * @return all the versions
   */
  List<Version> getVersionByProject(Long idProject);

  /**
   * Gets all the versions for a system.
   *
   * @param idService, the system id
   * @return all the versions
   */
  List<Version> getVersionsBySystemId(Long idService);
}
