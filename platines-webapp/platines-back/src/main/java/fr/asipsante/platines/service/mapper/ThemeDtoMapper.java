/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.service.mapper;

import fr.asipsante.platines.dto.ThemeDto;
import fr.asipsante.platines.entity.Theme;
import org.springframework.stereotype.Service;

/**
 * @author cnader
 */
@Service(value = "familyDtoMapper")
public class ThemeDtoMapper extends GenericDtoMapper<Theme, ThemeDto> {}
