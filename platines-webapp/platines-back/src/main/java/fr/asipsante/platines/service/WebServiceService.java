/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.service;

import fr.asipsante.platines.dto.SimulatedServiceDto;
import fr.asipsante.platines.dto.SimulatedServiceTreeDto;
import fr.asipsante.platines.entity.Project;
import fr.asipsante.platines.entity.Property;
import fr.asipsante.platines.entity.RelatedFiles;
import fr.asipsante.platines.entity.SimulatedService;
import fr.asipsante.platines.entity.TestCertificate;
import fr.asipsante.platines.entity.Theme;
import fr.asipsante.platines.entity.Version;
import fr.asipsante.platines.entity.enums.Role;
import java.util.List;

/**
 * The Project library service. All methods associated to entities {@link Theme}, {@link
 * SimulatedService}, {@link Version}, {@link Project}, {@link TestCertificate}, {@link
 * RelatedFiles}, and {@link Property}.
 *
 * @author apierre
 */
public interface WebServiceService {

  /**
   * List all the simulatedServices by the familyId.
   *
   * @param familyId the family id
   * @return thes simulated services associated to the familyId
   */
  List<SimulatedServiceDto> getSimulatedServicesByFamilyId(Long familyId);

  /**
   * List all the simulatedServices by the familyId.
   *
   * @param familyId the family id
   * @return thes simulated services associated to the familyId
   */
  List<SimulatedServiceDto> getSimulatedServicesByRoleAndFamilyId(Long familyId, Role role);

  /**
   * Gets all the simulated services register in the database.
   *
   * @return all the simulated services
   */
  List<SimulatedServiceDto> getAllSimulatedServices();

  /**
   * Gets a simulated service by is id.
   *
   * @param id the id
   * @return the simulated service as a DTO
   */
  SimulatedServiceDto getSimulatedServiceById(Long id);

  /**
   * Updates a simulated service in the database.
   *
   * @param simulatedServiceDto the simulated service dto
   */
  void updateSimulatedService(SimulatedServiceDto simulatedServiceDto);

  /**
   * Adds a simulated service in the database.
   *
   * @param simulatedServiceDto the simulated service dto
   */
  void createSimulatedService(SimulatedServiceDto simulatedServiceDto);

  SimulatedServiceTreeDto[] getSimulatedServicesTreeByFamilyId(Long id);
}
