/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.dao;

import fr.asipsante.platines.entity.Project;
import java.util.List;

/**
 * Interface provides methods required to link {@link Project} entity to a data source.
 *
 * @author aboittiaux
 */
public interface IProjectDao extends IGenericDao<Project, Long> {

  /**
   * Gets all the projects for a version.
   *
   * @param idVersion, the version id
   * @return all the projects
   */
  List<Project> getProjectsByVersion(Long idVersion);

  /**
   * Récupérer la référence d'un projet par le nom défini à l'intérieur de son fichier projet.
   *
   * @param definitionName le nom
   * @return un optional contenant le projet s'il existe.
   */
  List<Project> getProjectByFileName(String definitionName);
}
