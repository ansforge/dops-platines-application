/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.service.mapper;

import fr.asipsante.platines.dto.ProfileDto;
import fr.asipsante.platines.entity.Profile;
import org.springframework.stereotype.Service;

/**
 * @author cnader
 */
@Service(value = "profileDtoMapper")
public class ProfileDtoMapper extends GenericDtoMapper<Profile, ProfileDto> {}
