/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.service.mapper;

import fr.asipsante.platines.dto.SimulatedServiceTreeDto;
import fr.asipsante.platines.entity.SimulatedService;
import org.springframework.stereotype.Service;

/**
 * @author ssviridov
 */
@Service(value = "simulatedServiceTreeDtoMapper")
public class SimulatedServiceTreeDtoMapper
    extends GenericDtoMapper<SimulatedService, SimulatedServiceTreeDto> {}
