/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.service.mapper;

import fr.asipsante.platines.dto.ProjectResultPropertyDto;
import fr.asipsante.platines.entity.ProjectResultProperty;
import org.springframework.stereotype.Service;

/**
 * @author cnader
 */
@Service(value = "projectResultPropertyDtoMapper")
public class ProjectResultPropertyDtoMapper
    extends GenericDtoMapper<ProjectResultProperty, ProjectResultPropertyDto> {}
