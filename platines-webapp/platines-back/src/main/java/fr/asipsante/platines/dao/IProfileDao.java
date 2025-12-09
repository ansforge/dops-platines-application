/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.dao;

import fr.asipsante.platines.entity.Profile;

/**
 * Interface provides methods required to link {@link Profile} entity to a data source.
 *
 * @author apierre
 */
public interface IProfileDao extends IGenericDao<Profile, Long> {

  /**
   * Gets a profile by its id.
   *
   * @param id, profile id
   * @return the profile
   */
  Profile getByIdProfile(Long id);
}
