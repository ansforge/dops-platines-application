/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.service.mapper;

import fr.asipsante.platines.dto.ResourceBytesDto;
import fr.asipsante.platines.entity.Resource;
import org.springframework.stereotype.Service;

/**
 * @author cnader
 */
@Service(value = "resourceBytesDtoMapper")
public class ResourceBytesDtoMapper extends GenericDtoMapper<Resource, ResourceBytesDto> {}
