/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.service.mapper;

import fr.asipsante.platines.dto.ROperationDto;
import fr.asipsante.platines.entity.ROperationExpected;
import org.springframework.stereotype.Service;

/**
 * @author cnader
 */
@Service(value = "rOperationExpectedDtoMapper")
public class ROperationExpectedDtoMapper
    extends GenericDtoMapper<ROperationExpected, ROperationDto> {}
