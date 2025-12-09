/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.dao;

import fr.asipsante.platines.entity.Theme;
import fr.asipsante.platines.entity.enums.Role;
import java.util.Map;
import java.util.Set;

/**
 * Interface provides methods required to link {@link Theme} entity to a data source.
 *
 * @author apierre
 */
public interface IThemeDao extends IGenericDao<Theme, Long> {

  /**
   * Gets a family by its id, for anyone who asks.
   *
   * @param id, family id
   * @return the family
   */
  Theme safeFetchById(Long id);

  /** Gets families with covered role information. */
  Map<Theme, Set<Role>> getCoveredRoleMapping();
}
