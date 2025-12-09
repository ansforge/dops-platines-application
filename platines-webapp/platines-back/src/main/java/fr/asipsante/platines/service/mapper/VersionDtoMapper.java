/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.service.mapper;

import fr.asipsante.platines.dto.VersionDto;
import fr.asipsante.platines.entity.Version;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * @author cnader
 */
@Service(value = "versionDtoMapper")
public class VersionDtoMapper extends GenericDtoMapper<Version, VersionDto> {

  /** ModelMapper. */
  @Autowired private ModelMapper modelMapper;

  /** UserDtoMapper. */
  @Autowired
  @Qualifier("simulatedServiceDtoMapper")
  private SimulatedServiceDtoMapper simulatedServiceDtoMapper;

  /**
   * Converts a {@link Version} into a {@link VersionDto}.
   *
   * @param version, the {@link Version} to convert
   * @return a {@link VersionDto}
   */
  public VersionDto convertToVersionDto(Version version) {
    final VersionDto versionDto = modelMapper.map(version, VersionDto.class);
    if (version.getService() != null) {
      versionDto.setService(
          simulatedServiceDtoMapper.convertToSimulatedServiceDto(version.getService()));
    }
    return versionDto;
  }

  /**
   * Converts a {@link VersionDto} into a {@link Version}.
   *
   * @param versionDto, the {@link VersionDto} to convert
   * @return a {@link Version}
   */
  public Version convertToVersion(VersionDto versionDto) {
    final Version version = modelMapper.map(versionDto, Version.class);
    if (versionDto.getService() != null) {
      version.setService(
          simulatedServiceDtoMapper.convertToSimulatedService(versionDto.getService()));
    }
    return version;
  }
}
