/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.dao.impl;

import fr.asipsante.platines.dao.IProfileDao;
import fr.asipsante.platines.entity.Profile;
import org.springframework.stereotype.Repository;

/**
 * @author apierre
 */
@Repository(value = "profileDao")
public class ProfileDaoImpl extends GenericDaoImpl<Profile, Long> implements IProfileDao {

  @Override
  public Profile getByIdProfile(Long id) {
    return entityManager.find(Profile.class, id);
  }
}
