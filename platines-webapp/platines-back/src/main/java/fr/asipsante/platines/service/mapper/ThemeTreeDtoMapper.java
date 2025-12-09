/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.service.mapper;

import fr.asipsante.platines.dto.FamilyTreeDto;
import fr.asipsante.platines.entity.Theme;
import org.springframework.stereotype.Service;

/**
 * @author ssviridov
 */
@Service(value = "familyTreeDtoMapper")
public class ThemeTreeDtoMapper extends GenericDtoMapper<Theme, FamilyTreeDto> {}
