/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.controller;

import fr.asipsante.platines.dto.SimulatedServiceDto;
import fr.asipsante.platines.entity.enums.Role;
import fr.asipsante.platines.service.WebServiceService;
import java.util.List;
import javax.ws.rs.QueryParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller used to manage the simulated system.
 *
 * @author apierre
 */
@RequestMapping("/secure")
@RestController
public class SimulatedServiceController {

  /** The project library service. */
  @Autowired private WebServiceService webServiceService;

  /**
   * Lists all the simulated services.
   *
   * @return all the simulated services as DTO.
   */
  @GetMapping(value = "/system/getAll", produces = "application/json")
  public List<SimulatedServiceDto> getAll() {
    return webServiceService.getAllSimulatedServices();
  }

  /**
   * Gets a simulated service by is id.
   *
   * @param id the simulated service id
   * @return the simulated service
   */
  @GetMapping(value = "/system/get/{id}", produces = "application/json")
  public SimulatedServiceDto getSimulatedService(@PathVariable Long id) {
    return webServiceService.getSimulatedServiceById(id);
  }

  /**
   * Creates a simulated service.
   *
   * @param systemDto the simulated service to add
   */
  @PreAuthorize("hasRole('admin') or hasRole('manager')")
  @PostMapping(value = "/system/create", produces = "application/json")
  public void createSimulatedService(@RequestBody SimulatedServiceDto systemDto) {
    webServiceService.createSimulatedService(systemDto);
  }

  /**
   * Updates a simulated service.
   *
   * @param systemDto the simulated service to update
   */
  @PreAuthorize("hasRole('admin') or hasRole('manager')")
  @PostMapping(value = "/system/update", produces = "application/json")
  public void updateSimulatedService(@RequestBody SimulatedServiceDto systemDto) {
    webServiceService.updateSimulatedService(systemDto);
  }

  /**
   * List all the simulated services associated to a family.
   *
   * @param id the family id
   * @param coveredRole optional filtere : if provided, returnded system must be linked to test
   *     projects that simulated the reuired role.
   * @return the simulated services associated
   */
  @GetMapping(value = "/system/getService/{id}", produces = "application/json")
  public List<SimulatedServiceDto> getSimulatedServiceById(
      @PathVariable Long id, @QueryParam("coveredRole") Role coveredRole) {
    if (coveredRole == null) {
      return webServiceService.getSimulatedServicesByFamilyId(id);
    } else {
      return webServiceService.getSimulatedServicesByRoleAndFamilyId(id, coveredRole);
    }
  }
}
