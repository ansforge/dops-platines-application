/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.service.impl.common;

import fr.asipsante.platines.dao.IProjectDao;
import fr.asipsante.platines.dao.IVersionDao;
import fr.asipsante.platines.dto.VersionDto;
import fr.asipsante.platines.entity.Project;
import fr.asipsante.platines.entity.Version;
import fr.asipsante.platines.entity.enums.Role;
import fr.asipsante.platines.service.VersionService;
import fr.asipsante.platines.service.mapper.SimulatedServiceDtoMapper;
import fr.asipsante.platines.service.mapper.VersionDtoMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * The project library service.
 *
 * @author apierre
 */
@Service(value = "VersionService")
public class VersionServiceImpl implements VersionService {

  /** LOGGER. */
  private static final Logger LOGGER = LoggerFactory.getLogger(VersionServiceImpl.class);

  /** IVersionDao. */
  @Autowired
  @Qualifier("versionDao")
  private IVersionDao versionDao;

  /** IProjectDao. */
  @Autowired
  @Qualifier("projectDao")
  private IProjectDao projectDao;

  @Autowired
  @Qualifier("versionDtoMapper")
  private VersionDtoMapper versionDtoMapper;

  @Autowired
  @Qualifier("simulatedServiceDtoMapper")
  private SimulatedServiceDtoMapper simulatedServiceDtoMapper;

  /** {@inheritDoc} */
  @Override
  @Transactional(readOnly = true)
  public List<VersionDto> getAllVersions() {
    final List<Version> versions = versionDao.getAll();
    final List<VersionDto> versionsDto = new ArrayList<>();
    versions.forEach(version -> versionsDto.add(versionDtoMapper.convertToVersionDto(version)));
    return versionsDto;
  }

  /** {@inheritDoc} */
  @Override
  @Transactional(readOnly = true)
  public List<VersionDto> getVersionsByFamilyId(Long familyId) {
    final List<VersionDto> versionsDto = new ArrayList<>();
    final List<Version> versions = versionDao.getVersionsByFamilyId(familyId);
    versions.forEach(version -> versionsDto.add(versionDtoMapper.convertToVersionDto(version)));
    return versionsDto;
  }

  /** {@inheritDoc} */
  @Override
  @Transactional(readOnly = true)
  public VersionDto getVersion(Long id) {
    return versionDtoMapper.convertToVersionDto(versionDao.getById(id));
  }

  /** {@inheritDoc} */
  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public void createVersion(VersionDto versionDto) {
    versionDao.save(versionDtoMapper.convertToVersion(versionDto));
  }

  /** {@inheritDoc} */
  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public void updateVersion(VersionDto versionDto) {
    final Version version = versionDao.getById(versionDto.getId());
    version.setService(simulatedServiceDtoMapper.convertToEntity(versionDto.getService()));
    version.setDescription(versionDto.getDescription());
    version.setVisibility(versionDto.isVisibility());
    version.setLabel(versionDto.getLabel());
    versionDao.update(version);
  }

  /** {@inheritDoc} */
  @Override
  @Transactional(readOnly = true)
  public List<VersionDto> getVersionsByUser() {
    final List<VersionDto> versionsDto = new ArrayList<>();
    final List<Version> versions = versionDao.getVersionsByUser();
    versions.forEach(version -> versionsDto.add(versionDtoMapper.convertToVersionDto(version)));
    return versionsDto;
  }

  /** {@inheritDoc} */
  @Override
  @Transactional(readOnly = true)
  public List<VersionDto> getVersionByProject(Long idProject) {
    final List<VersionDto> versionsDto = new ArrayList<>();
    final List<Version> versions = versionDao.getVersionByProject(idProject);
    versions.forEach(version -> versionsDto.add(versionDtoMapper.convertToVersionDto(version)));
    return versionsDto;
  }

  /** {@inheritDoc} */
  @Override
  @Transactional(readOnly = true)
  public List<VersionDto> getVersionByUserByProject(Long idProject) {
    final List<VersionDto> versionsDto = new ArrayList<>();
    final List<Version> versions = versionDao.getVersionByProject(idProject);
    final List<Version> versionsVisible = new ArrayList<>();
    versions.forEach(
        v -> {
          if (v.getService().getTheme().getVisibility() && v.getVisibility()) {
            versionsVisible.add(v);
          }
        });
    versionsVisible.forEach(
        version -> versionsDto.add(versionDtoMapper.convertToVersionDto(version)));
    return versionsDto;
  }

  /** {@inheritDoc} */
  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public void updateCompatibility(Long idProject, Long idVersion) {
    final Project project = projectDao.getById(idProject);
    final Predicate<Version> pred = v -> v.getId().equals(idVersion);
    if (project.getVersions().stream().noneMatch(pred)) {
      project.getVersions().add(versionDao.getById(idVersion));
    } else {
      project.getVersions().removeIf(pred);
    }
  }

  /** {@inheritDoc} */
  @Override
  @Transactional(readOnly = true)
  public List<VersionDto> getVersionsBySystemId(Long systemId) {
    final List<VersionDto> versionsDto = new ArrayList<>();
    final List<Version> versions = versionDao.getVersionsBySystemId(systemId);
    versions.forEach(version -> versionsDto.add(versionDtoMapper.convertToVersionDto(version)));
    return versionsDto;
  }

  /** {@inheritDoc} */
  @Override
  @Transactional(readOnly = true)
  public List<VersionDto> getVersionsBySystemIdWithRole(Long systemId, Role coveredRole) {
    final List<Version> versions = versionDao.getVersionsBySystemId(systemId);
    return versions.stream()
        .filter(version -> version.getProjects().stream().anyMatch(p -> p.getRole() == coveredRole))
        .map(version -> versionDtoMapper.convertToVersionDto(version))
        .collect(Collectors.toList());
  }
}
