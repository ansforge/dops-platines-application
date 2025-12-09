/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.service;

import fr.asipsante.platines.dto.ResourceDto;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

/**
 * Resource Service.
 *
 * @author cnader
 */
public interface ResourceService {

  /**
   * Persists a new resource in the database.
   *
   * @param associationId association Id
   * @param resourceType resource Type
   * @param file the file to persist
   * @return ResourceDto
   */
  String saveFile(MultipartFile file, Long associationId, String resourceType);

  /**
   * Get resources belonging to an association from the database.
   *
   * @param associationId association id
   * @return the resources list
   */
  List<ResourceDto> getResourcesByAssociation(Long associationId);

  /**
   * Get resources belonging to a version and its parents from the database.
   *
   * @param id, version id
   * @return the resources list
   */
  List<ResourceDto> getResourcesByVersion(Long id);

  /**
   * Get all resources from the database.
   *
   * @return the resources list
   */
  List<ResourceDto> getAllResources();

  /**
   * Updates a resource in the database.
   *
   * @param resourceDto the resource to update
   */
  void updateResource(ResourceDto resourceDto);

  /**
   * Deletes a resource in the database.
   *
   * @param resourceDto the resource to delete
   */
  void deleteResource(ResourceDto resourceDto);
}
