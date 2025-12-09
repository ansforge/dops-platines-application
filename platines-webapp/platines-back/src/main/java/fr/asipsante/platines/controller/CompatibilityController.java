/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.controller;

import fr.asipsante.platines.dto.VersionDto;
import fr.asipsante.platines.entity.enums.Role;
import fr.asipsante.platines.service.VersionService;
import java.util.List;
import javax.ws.rs.QueryParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author aboittiaux
 */
@RequestMapping("/secure/")
@RestController
public class CompatibilityController {

  /** The project library service. */
  @Autowired VersionService versionService;

  /**
   * Updates the compatibility between project and version.
   *
   * @param idProject project to update
   * @param idVersion version to update
   */
  @PreAuthorize("hasRole('admin') or hasRole('manager')")
  @PostMapping(headers = "Content-Type= multipart/form-data", value = "compatibility/mapping")
  public void mappingProjetVersion(
      @RequestParam("idProject") Long idProject, @RequestParam("idVersion") Long idVersion) {
    versionService.updateCompatibility(idProject, idVersion);
  }

  /**
   * Gets all the compatible versions for a specific system.
   *
   * @param idSystem id of the specific system
   * @param coveredRole optional role filter. If supplied, only versions with project that simulate
   *     this role are returned.
   * @return all the compatible versions
   */
  @GetMapping(produces = "application/json", value = "compatibility/versions/{idSystem}")
  public List<VersionDto> getVersionsBySystemId(
      @PathVariable Long idSystem, @QueryParam("coveredRole") Role coveredRole) {
    if (coveredRole == null) {
      return versionService.getVersionsBySystemId(idSystem);
    } else {
      return versionService.getVersionsBySystemIdWithRole(idSystem, coveredRole);
    }
  }
}
