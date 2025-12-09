/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.service.mapper;

import fr.asipsante.platines.dto.ROperationDto;
import fr.asipsante.platines.entity.ROperationUnexpected;
import org.springframework.stereotype.Service;

/**
 * @author cnader
 */
@Service(value = "rOperationUnexpectedDtoMapper")
public class ROperationUnexpectedDtoMapper
    extends GenericDtoMapper<ROperationUnexpected, ROperationDto> {}
