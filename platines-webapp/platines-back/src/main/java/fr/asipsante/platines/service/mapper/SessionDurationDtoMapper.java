/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.service.mapper;

import fr.asipsante.platines.dto.SessionDurationDto;
import fr.asipsante.platines.entity.SessionDuration;
import org.springframework.stereotype.Service;

/**
 * @author cnader
 */
@Service(value = "sessionDurationDtoMapper")
public class SessionDurationDtoMapper
    extends GenericDtoMapper<SessionDuration, SessionDurationDto> {}
