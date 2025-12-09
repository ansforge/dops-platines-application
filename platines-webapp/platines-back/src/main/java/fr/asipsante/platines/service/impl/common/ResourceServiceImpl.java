/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.service.impl.common;

import fr.asipsante.platines.dao.IAssociationDao;
import fr.asipsante.platines.dao.IResourceDao;
import fr.asipsante.platines.dao.ISimulatedServiceDao;
import fr.asipsante.platines.dao.IThemeDao;
import fr.asipsante.platines.dao.IVersionDao;
import fr.asipsante.platines.dto.ResourceDto;
import fr.asipsante.platines.entity.Association;
import fr.asipsante.platines.entity.Resource;
import fr.asipsante.platines.entity.SimulatedService;
import fr.asipsante.platines.entity.Theme;
import fr.asipsante.platines.entity.Version;
import fr.asipsante.platines.entity.enums.FileType;
import fr.asipsante.platines.exception.ResourceException;
import fr.asipsante.platines.service.ResourceService;
import fr.asipsante.platines.service.mapper.ResourceDtoMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * Implementation of {@link ResourceService}.
 *
 * @author cnader
 */
@Service(value = "ResourceService")
public class ResourceServiceImpl implements ResourceService {

  /** RESOURCE_LOAD_ERROR. */
  private static final String RESOURCE_PERSIST_ERROR = "Can not load the resource file.";

  /** Resource DAO. */
  @Autowired
  @Qualifier("resourceDao")
  private IResourceDao resourceDao;

  /** Association DAO. */
  @Autowired
  @Qualifier("associationDao")
  private IAssociationDao associationDao;

  /** Theme DAO. */
  @Autowired private IThemeDao themeDao;

  /** Web Service DAO. */
  @Autowired
  @Qualifier("simulatedServiceDao")
  private ISimulatedServiceDao simulatedServiceDao;

  /** Version DAO. */
  @Autowired
  @Qualifier("versionDao")
  private IVersionDao versionDao;

  /** ResourceDtoMapper. */
  @Autowired
  @Qualifier("resourceDtoMapper")
  private ResourceDtoMapper resourceDtoMapper;

  /** DateConverter. */
  @Autowired private DateConverter dateConverter;

  // helper method, check if string is in enum object
  private static boolean fileTypeContains(String str) {
    for (final FileType ft : FileType.values()) {
      if (ft.name().equals(str)) {
        return true;
      }
    }
    return false;
  }

  /**
   * {@inheritDoc}
   *
   * @return response
   */
  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public String saveFile(MultipartFile file, Long associationId, String resourceType) {
    String response = "";
    try {
      Resource resource = new Resource();
      final Association association = associationDao.getById(associationId);
      resource.setFile(file.getBytes());
      resource.setFileName(file.getOriginalFilename());
      resource.setDateUpload(dateConverter.convertToUTC(new Date()));
      resource.setAssociation(association);
      switch (association
          .getAssociationType()) { // Just using more readable code BUT, this seems to be a case for
          // polymorphy, why use explicit logic ?
        case FAMILY -> resource.setTheme(themeDao.getById(association.getId()));
        case SERVICE -> resource.setTheme(
            simulatedServiceDao.getById(association.getId()).getTheme());
        case VERSION -> resource.setTheme(
            versionDao.getById(association.getId()).getService().getTheme());
        default -> throw new IllegalArgumentException(
            "Unsupported "
                + Association.RANK.class.getName()
                + " value "
                + association.getAssociationType().name());
      }
      if (fileTypeContains(resourceType)) {
        resource.setFileType(FileType.valueOf(resourceType));
      }
      resource = resourceDao.save(resource);
      response = resource.getId().toString();
    } catch (final IOException e) {
      throw new ResourceException(e, RESOURCE_PERSIST_ERROR);
    }
    return response;
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public void deleteResource(ResourceDto resourceDto) {
    final Resource resource = resourceDao.getById(resourceDto.getId());
    resourceDao.delete(resource);
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public void updateResource(ResourceDto resourceDto) {
    final Resource resource = resourceDao.getById(resourceDto.getId());
    if (fileTypeContains(resourceDto.getResourceType())) {
      resource.setFileType(FileType.valueOf(resourceDto.getResourceType()));
    }
    resourceDao.update(resource);
  }

  @Transactional
  @Override
  public List<ResourceDto> getAllResources() {
    final List<ResourceDto> resourcesDto = new ArrayList<>();
    final List<Resource> resources = resourceDao.getAll();
    for (final Resource resource : resources) {
      resourcesDto.add(resourceDtoMapper.convertToDto(resource));
    }
    return resourcesDto;
  }

  @Transactional
  @Override
  public List<ResourceDto> getResourcesByAssociation(Long associationId) {
    final List<ResourceDto> resourcesDto = new ArrayList<>();
    final List<Resource> resources = resourceDao.getResourcesByAssociation(associationId);
    for (final Resource resource : resources) {
      resourcesDto.add(resourceDtoMapper.convertToDto(resource));
    }
    return resourcesDto;
  }

  @Override
  public List<ResourceDto> getResourcesByVersion(Long versionId) {
    final List<ResourceDto> resourcesDto = new ArrayList<>();
    final Version version = versionDao.getById(versionId);
    final SimulatedService webService = version.getService();
    final Theme theme = webService.getTheme();
    final List<Resource> resources = new ArrayList<>();
    resources.addAll(resourceDao.getResourcesByAssociation(versionId));
    resources.addAll(resourceDao.getResourcesByAssociation(webService.getId()));
    resources.addAll(resourceDao.getResourcesByAssociation(theme.getId()));
    for (final Resource resource : resources) {
      resourcesDto.add(resourceDtoMapper.convertToDto(resource));
    }
    return resourcesDto;
  }
}
