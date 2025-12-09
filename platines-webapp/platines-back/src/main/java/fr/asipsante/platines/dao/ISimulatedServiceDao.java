/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.dao;

import fr.asipsante.platines.entity.SimulatedService;
import fr.asipsante.platines.entity.enums.Role;
import java.util.List;

/**
 * Interface provides methods required to link {@link SimulatedService} entity to a data source.
 *
 * @author apierre
 */
public interface ISimulatedServiceDao extends IGenericDao<SimulatedService, Long> {

  /**
   * List all the Simulated Services associated to a Family.
   *
   * @param familyId, the family id associate to the simulated services
   * @return the simulated services associated
   */
  List<SimulatedService> getSimulatedServicesByFamilyId(Long familyId);

  List<SimulatedService> getSimulatedServicesForRoleInFamily(Long familyId, Role role);
}
