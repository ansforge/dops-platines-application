/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.service.mapper;

import fr.asipsante.platines.dto.PropertyDto;
import fr.asipsante.platines.entity.Property;
import org.springframework.stereotype.Service;

/**
 * @author cnader
 */
@Service(value = "propertyDtoMapper")
public class PropertyDtoMapper extends GenericDtoMapper<Property, PropertyDto> {}
