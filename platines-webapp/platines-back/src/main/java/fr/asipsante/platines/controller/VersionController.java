/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.controller;

import fr.asipsante.platines.dto.UserDto;
import fr.asipsante.platines.dto.VersionDto;
import fr.asipsante.platines.service.UserService;
import fr.asipsante.platines.service.VersionService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author apierre
 */
@RequestMapping("/secure")
@RestController
public class VersionController {

  /** The profile id for an admin in database. */
  private static final Long ADMIN_ID = 1L;

  /** The project library service. */
  @Autowired private VersionService versionService;

  /** The user service. */
  @Autowired private UserService userService;

  /**
   * Gets all the versions.
   *
   * @param token, the user token
   * @return the list of all the versions
   */
  @GetMapping(value = "/version/getAll", produces = "application/json")
  public List<VersionDto> getAllVersions(@RequestHeader(value = "Authorization") String token) {
    List<VersionDto> versionsDto;
    final UserDto userDto = userService.getUserByToken(token);
    if (!userDto.getProfile().getId().equals(ADMIN_ID)) {
      versionsDto = versionService.getVersionsByUser();
    } else {
      versionsDto = versionService.getAllVersions();
    }
    return versionsDto;
  }

  /**
   * Gets all the versions associated to a family.
   *
   * @param id family id
   * @return the list of the versions
   */
  @GetMapping(value = "/version/getVersions/{id}", produces = "application/json")
  public List<VersionDto> getVersionsByFamilyId(@PathVariable Long id) {
    return versionService.getVersionsByFamilyId(id);
  }

  /**
   * Gets a version by its id.
   *
   * @param id, id of the version to find
   * @return the version
   */
  @PreAuthorize("hasRole('admin') or hasRole('manager')")
  @GetMapping(value = "/version/get/{id}", produces = "application/json")
  public VersionDto getVersion(@PathVariable Long id) {
    return versionService.getVersion(id);
  }

  /**
   * Persists a new version in database.
   *
   * @param version, the version to persist
   */
  @PreAuthorize("hasRole('admin') or hasRole('manager')")
  @PostMapping(value = "/version/create", produces = "application/json")
  public void createVersion(@RequestBody VersionDto version) {
    versionService.createVersion(version);
  }

  /**
   * Updates a version.
   *
   * @param version, the version with updates
   */
  @PreAuthorize("hasRole('admin') or hasRole('manager')")
  @PostMapping(value = "/version/update", produces = "application/json")
  public void updateVersion(@RequestBody VersionDto version) {
    versionService.updateVersion(version);
  }
}
