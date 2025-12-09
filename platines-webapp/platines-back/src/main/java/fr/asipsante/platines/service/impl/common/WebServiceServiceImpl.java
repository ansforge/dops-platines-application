/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.service.impl.common;

import fr.asipsante.platines.dao.ISimulatedServiceDao;
import fr.asipsante.platines.dto.SimulatedServiceDto;
import fr.asipsante.platines.dto.SimulatedServiceTreeDto;
import fr.asipsante.platines.entity.SimulatedService;
import fr.asipsante.platines.entity.enums.Role;
import fr.asipsante.platines.service.WebServiceService;
import fr.asipsante.platines.service.mapper.SimulatedServiceDtoMapper;
import fr.asipsante.platines.service.mapper.SimulatedServiceTreeDtoMapper;
import java.util.ArrayList;
import java.util.List;
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
@Service(value = "WebServiceService")
public class WebServiceServiceImpl implements WebServiceService {

  /** LOGGER. */
  private static final Logger LOGGER = LoggerFactory.getLogger(WebServiceServiceImpl.class);

  /** ISimulatedServiceDao. */
  @Autowired
  @Qualifier("simulatedServiceDao")
  private ISimulatedServiceDao simulatedServiceDao;

  @Autowired
  @Qualifier("simulatedServiceDtoMapper")
  private SimulatedServiceDtoMapper simulatedServiceDtoMapper;

  @Autowired
  @Qualifier("simulatedServiceTreeDtoMapper")
  private SimulatedServiceTreeDtoMapper simulatedServiceTreeDtoMapper;

  /** {@inheritDoc} */
  @Override
  @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
  public List<SimulatedServiceDto> getAllSimulatedServices() {
    final List<SimulatedServiceDto> simulatedServicesDto = new ArrayList<>();
    final List<SimulatedService> simulatedServices = simulatedServiceDao.getAll();
    for (final SimulatedService simulatedService : simulatedServices) {
      simulatedServicesDto.add(simulatedServiceDtoMapper.convertToDto(simulatedService));
    }
    return simulatedServicesDto;
  }

  /** {@inheritDoc} */
  @Override
  @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
  public SimulatedServiceDto getSimulatedServiceById(Long id) {
    return simulatedServiceDtoMapper.convertToDto(simulatedServiceDao.getById(id));
  }

  /** {@inheritDoc} */
  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public void updateSimulatedService(SimulatedServiceDto simulatedServiceDto) {
    simulatedServiceDao.update(simulatedServiceDtoMapper.convertToEntity(simulatedServiceDto));
  }

  /** {@inheritDoc} */
  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public void createSimulatedService(SimulatedServiceDto simulatedServiceDto) {
    simulatedServiceDao.save(simulatedServiceDtoMapper.convertToEntity(simulatedServiceDto));
  }

  @Override
  public SimulatedServiceTreeDto[] getSimulatedServicesTreeByFamilyId(Long familyId) {
    final List<SimulatedService> simulatedServices =
        simulatedServiceDao.getSimulatedServicesByFamilyId(familyId);
    final List<SimulatedServiceTreeDto> simulatedServicesTreeDto = new ArrayList<>();
    for (final SimulatedService simulatedService : simulatedServices) {
      simulatedServicesTreeDto.add(simulatedServiceTreeDtoMapper.convertToDto(simulatedService));
    }
    return simulatedServicesTreeDto.toArray(new SimulatedServiceTreeDto[0]);
  }

  /** {@inheritDoc} */
  @Override
  @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
  public List<SimulatedServiceDto> getSimulatedServicesByFamilyId(Long familyId) {
    final List<SimulatedService> simulatedServices =
        simulatedServiceDao.getSimulatedServicesByFamilyId(familyId);
    final List<SimulatedServiceDto> simulatedServicesDto = new ArrayList<>();
    for (final SimulatedService simulatedService : simulatedServices) {
      simulatedServicesDto.add(simulatedServiceDtoMapper.convertToDto(simulatedService));
    }
    return simulatedServicesDto;
  }

  /** {@inheritDoc} */
  @Override
  @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
  public List<SimulatedServiceDto> getSimulatedServicesByRoleAndFamilyId(Long familyId, Role role) {
    final List<SimulatedService> simulatedService =
        simulatedServiceDao.getSimulatedServicesForRoleInFamily(familyId, role);
    return simulatedService.stream()
        .map(service -> simulatedServiceDtoMapper.convertToDto(service))
        .collect(Collectors.toList());
  }
}
