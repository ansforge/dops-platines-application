/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.service;

import fr.asipsante.platines.dto.VersionDto;
import fr.asipsante.platines.entity.Version;
import fr.asipsante.platines.entity.enums.Role;
import java.util.List;

/**
 * The version service. All methods associated to entities {@link Version}.
 *
 * @author apierre
 */
public interface VersionService {

  /**
   * Gets all the versions save in the database.
   *
   * @return all the versions
   */
  List<VersionDto> getAllVersions();

  /**
   * Gets the versions in database visible for user.
   *
   * @return the versions visible for a user
   */
  List<VersionDto> getVersionsByUser();

  /**
   * Gets the versions associated to a family.
   *
   * @param familyId the family id
   * @return the versions associated to the family
   */
  List<VersionDto> getVersionsByFamilyId(Long familyId);

  /**
   * Gets the version by is id.
   *
   * @param id the id
   * @return the version
   */
  VersionDto getVersion(Long id);

  /**
   * Saves the version in the database.
   *
   * @param versionDto the version dto
   */
  void createVersion(VersionDto versionDto);

  /**
   * Update a version in the database.
   *
   * @param versionDto the version dto
   */
  void updateVersion(VersionDto versionDto);

  List<VersionDto> getVersionByProject(Long idProject);

  /**
   * Gets all the version for a project and visible by an user.
   *
   * @param idProject the id project
   * @return the list of versions visible by an user for the project
   */
  List<VersionDto> getVersionByUserByProject(Long idProject);

  /**
   * Updates the compatibility table.
   *
   * @param idProject the id project
   * @param idVersion the id version
   */
  void updateCompatibility(Long idProject, Long idVersion);

  /**
   * Gets all the version for a simulated service.
   *
   * @param systemId the system id
   * @return a versions list
   */
  List<VersionDto> getVersionsBySystemId(Long systemId);

  /**
   * Gets all the version for a simulated service with tests to simulate the required role.
   *
   * @param systemId the system id
   * @param coveredRole the simulated role to cover. A version will be selected if it is linked at
   *     least to one project with tois role.
   * @return a versions list
   */
  List<VersionDto> getVersionsBySystemIdWithRole(Long systemId, Role coveredRole);
}
