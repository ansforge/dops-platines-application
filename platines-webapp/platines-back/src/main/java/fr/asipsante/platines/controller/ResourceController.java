/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.controller;

import fr.asipsante.platines.dto.ResourceDto;
import fr.asipsante.platines.service.ResourceService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * The type Resource controller.
 *
 * @author cnader
 */
@RequestMapping("/secure")
@RestController
public class ResourceController {

  /** The resource service. */
  @Autowired
  @Qualifier("ResourceService")
  private ResourceService resourceService;

  /**
   * Save a resource file in the database.
   *
   * @param resourceFile the resource file
   * @param associationId the association id
   * @param resourceType the resource type
   * @return String string
   */
  @PreAuthorize("hasRole('admin') or hasRole('manager')")
  @PostMapping(value = "/resource/add", headers = "Content-Type= multipart/form-data")
  public String saveFile(
      @RequestParam MultipartFile resourceFile,
      @RequestParam Long associationId,
      @RequestParam String resourceType) {
    return resourceService.saveFile(resourceFile, associationId, resourceType);
  }

  /**
   * Deletes a resource.
   *
   * @param resourceDto resource to delete
   */
  @PreAuthorize("hasRole('admin') or hasRole('manager')")
  @PostMapping(value = "/resource/delete", produces = "application/json")
  public void delete(@RequestBody ResourceDto resourceDto) {
    resourceService.deleteResource(resourceDto);
  }

  /**
   * Updates a resource.
   *
   * @param resourceDto resource to update
   */
  @PreAuthorize("hasRole('admin') or hasRole('manager')")
  @PostMapping(value = "/resource/update", produces = "application/json")
  public void update(@RequestBody ResourceDto resourceDto) {
    resourceService.updateResource(resourceDto);
  }

  /**
   * Gets resources by association.
   *
   * @param id the association id
   * @return the resources
   */
  @PreAuthorize("hasRole('admin') or hasRole('manager')")
  @GetMapping(value = "resource/getByAssociation/{id}", produces = "application/json")
  public List<ResourceDto> getResourcesByAssociation(@PathVariable Long id) {
    return resourceService.getResourcesByAssociation(id);
  }

  /**
   * Gets resources by version.
   *
   * @param id the version id
   * @return the resources
   */
  @PreAuthorize("hasRole('admin') or hasRole('manager')")
  @GetMapping(value = "resource/getByVersion/{id}", produces = "application/json")
  public List<ResourceDto> getResourcesByVersion(@PathVariable Long id) {
    return resourceService.getResourcesByVersion(id);
  }

  /**
   * Gets all resources.
   *
   * @return the resources
   */
  @PreAuthorize("hasRole('admin') or hasRole('manager')")
  @GetMapping(value = "resource/getAll", produces = "application/json")
  public List<ResourceDto> getAllResources() {
    return resourceService.getAllResources();
  }
}
