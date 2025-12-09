/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.controller;

import fr.asipsante.platines.dto.FamilyTreeDto;
import fr.asipsante.platines.dto.SimulatedServiceTreeDto;
import fr.asipsante.platines.dto.ThemeDto;
import fr.asipsante.platines.dto.ThemeRolesDto;
import fr.asipsante.platines.dto.VersionDto;
import fr.asipsante.platines.service.ThemeService;
import fr.asipsante.platines.service.VersionService;
import fr.asipsante.platines.service.WebServiceService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller used to manage the family.
 *
 * @author apierre
 */
@RequestMapping("/secure")
@RestController
public class ThemeController {

  /** The project library service. */
  @Autowired private ThemeService themeService;

  @Autowired private WebServiceService webServiceService;

  @Autowired private VersionService versionService;

  /**
   * List all the families register in database.
   *
   * @return all the families as DTO
   */
  @GetMapping(value = "/family/getAll", produces = "application/json")
  public List<ThemeDto> getAll() {
    List<ThemeDto> familiesDto = null;
    familiesDto = themeService.getAllFamilies();
    return familiesDto;
  }

  /**
   * Récupérer les données sur les rôles couvets par chaque famille.
   *
   * @return All role coverage data as {@link ThemeRolesDto}
   */
  @GetMapping(value = "/family/role-coverage/getAll", produces = "application/json")
  public List<ThemeRolesDto> getFamilyRoles() {
    return themeService.getThemeRoleCoverage();
  }

  /**
   * List all the families registered in the database using the tree-friendly dtos
   *
   * @return all the families as DTO with child web services with child versions
   */
  @GetMapping(value = "/family/getAllTree", produces = "application/json")
  public List<FamilyTreeDto> getAllAsTree() {
    List<FamilyTreeDto> themesDto = themeService.getAllFamiliesAsTree();
    for (FamilyTreeDto family : themesDto) {
      family.setSystems(webServiceService.getSimulatedServicesTreeByFamilyId(family.getId()));
      for (SimulatedServiceTreeDto simulatedService : family.getSystems()) {
        simulatedService.setVersions(
            versionService
                .getVersionsBySystemId(simulatedService.getId())
                .toArray(new VersionDto[0]));
      }
    }
    return themesDto;
  }

  /**
   * Persists a new family.
   *
   * @param familyDto the new family to persist
   */
  @PreAuthorize("hasRole('admin')")
  @PostMapping(value = "/family/create", produces = "application/json")
  public void create(@RequestBody ThemeDto familyDto) {
    themeService.createFamily(familyDto);
  }

  /**
   * Gets a family by is id.
   *
   * @param id id of the family to find
   * @return the family
   */
  @PreAuthorize("hasRole('admin') or hasRole('manager')")
  @GetMapping(value = "/family/get/{id}", produces = "application/json")
  public ThemeDto getById(@PathVariable Long id) {
    return themeService.getFamilyById(id);
  }

  /**
   * Updates a family.
   *
   * @param familyDto the family with updates
   */
  @PreAuthorize("hasRole('admin')")
  @PostMapping(value = "/family/update", produces = "application/json")
  public void update(@RequestBody ThemeDto familyDto) {
    themeService.updateFamily(familyDto);
  }

  /**
   * Deletes a family from database.
   *
   * @param id id of the family to delete
   */
  @PreAuthorize("hasRole('admin')")
  @DeleteMapping(value = "/family/delete/{id}", produces = "application/json")
  public void delete(@PathVariable Long id) {
    themeService.deleteFamily(id);
  }
}
