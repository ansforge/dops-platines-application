/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.service.mapper;

import fr.asipsante.platines.dto.ResourceDto;
import fr.asipsante.platines.entity.Resource;
import org.springframework.stereotype.Service;

/**
 * @author cnader
 */
@Service(value = "resourceDtoMapper")
public class ResourceDtoMapper extends GenericDtoMapper<Resource, ResourceDto> {}
