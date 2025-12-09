/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.dao;

import fr.asipsante.platines.entity.Application;
import java.util.List;

/**
 * Interface provides methods required to link {@link Application} entity to a data source.
 *
 * @author aboittiaux
 */
public interface IApplicationDao extends IGenericDao<Application, Long> {

  /**
   * Gets all applications for a user.
   *
   * @param id, the user id.
   * @return all the applications
   */
  List<Application> getApplicationsByUser(Long id);

  /**
   * Gets all application for a trustChain.
   *
   * @param idChain, the trustChain id
   * @return all the applications
   */
  List<Application> getApplicationsByIdChain(Long idChain);
}
