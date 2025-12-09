/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.service.mapper;

import fr.asipsante.platines.dto.SimulatedServiceDto;
import fr.asipsante.platines.entity.SimulatedService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * @author cnader
 */
@Service(value = "simulatedServiceDtoMapper")
public class SimulatedServiceDtoMapper
    extends GenericDtoMapper<SimulatedService, SimulatedServiceDto> {

  /** ModelMapper. */
  @Autowired private ModelMapper modelMapper;

  /** UserDtoMapper. */
  @Autowired
  @Qualifier("familyDtoMapper")
  private ThemeDtoMapper familyDtoMapper;

  /**
   * Converts a {@link SimulatedService} into a {@link SimulatedServiceDto}.
   *
   * @param simulatedService, the simulatedService to convert
   * @return a {@link SimulatedServiceDto}
   */
  public SimulatedServiceDto convertToSimulatedServiceDto(SimulatedService simulatedService) {
    final SimulatedServiceDto simulatedServiceDto =
        modelMapper.map(simulatedService, SimulatedServiceDto.class);
    simulatedServiceDto.setTheme(familyDtoMapper.convertToDto(simulatedService.getTheme()));
    return simulatedServiceDto;
  }

  /**
   * Converts a {@link SimulatedServiceDto} into a {@link SimulatedService}.
   *
   * @param simulatedServiceDto, the {@link SimulatedServiceDto} to convert
   * @return a {@link SimulatedService}
   */
  public SimulatedService convertToSimulatedService(SimulatedServiceDto simulatedServiceDto) {
    final SimulatedService simulatedService =
        modelMapper.map(simulatedServiceDto, SimulatedService.class);
    if (simulatedServiceDto.getTheme() != null) {
      simulatedService.setTheme(familyDtoMapper.convertToEntity(simulatedServiceDto.getTheme()));
    }
    return simulatedService;
  }
}
