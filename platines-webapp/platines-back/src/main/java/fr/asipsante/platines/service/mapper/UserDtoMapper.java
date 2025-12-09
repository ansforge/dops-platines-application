/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.service.mapper;

import fr.asipsante.platines.dao.IGenericDao;
import fr.asipsante.platines.dao.IProfileDao;
import fr.asipsante.platines.dto.ThemeDto;
import fr.asipsante.platines.dto.UserDto;
import fr.asipsante.platines.entity.Profile;
import fr.asipsante.platines.entity.User;
import fr.asipsante.platines.entity.UserFamily;
import java.util.Date;
import java.util.HashSet;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * The type User dto mapper.
 *
 * @author cnader
 */
@Service(value = "userDtoMapper")
public class UserDtoMapper extends GenericDtoMapper<User, UserDto> {

  /** ModelMapper. */
  @Autowired private ModelMapper modelMapper;

  /** IProfileDao. */
  private IProfileDao profileDao;

  /** ThemeDtoMapper. */
  @Autowired
  @Qualifier("familyDtoMapper")
  private ThemeDtoMapper familyDtoMapper;

  /** ProfileDtoMapper. */
  @Autowired
  @Qualifier("profileDtoMapper")
  private ProfileDtoMapper profileDtoMapper;

  /**
   * Constructor.
   *
   * @param profileDao the profile dao
   */
  @Autowired
  public UserDtoMapper(@Qualifier("profileDao") IGenericDao<Profile, Long> profileDao) {
    this.profileDao = (IProfileDao) profileDao;
  }

  /**
   * Converts a {@link UserDto} into {@link User}.
   *
   * @param userDto the user dto
   * @return a user
   */
  public User convertToUser(UserDto userDto) {

    final User user = modelMapper.map(userDto, User.class);

    if (userDto.getProfile() != null) {
      user.setProfile(profileDtoMapper.convertToEntity(userDto.getProfile()));
      user.setProfile(profileDao.getByIdProfile(user.getProfile().getId()));
    }
    if (!userDto.getFamilies().isEmpty()) {
      user.setFamilies(new HashSet<>());
      for (final ThemeDto familyDto : userDto.getFamilies()) {
        user.addFamily(familyDtoMapper.convertToEntity(familyDto));
      }
    }

    user.setCreationDate(new Date());
    user.setDateLastTryAuthentication(new Date());
    user.setNbAuthentFailed(0);

    return user;
  }

  /**
   * Converts a {@link User} into {@link UserDto}.
   *
   * @param user the user
   * @return a userDto
   */
  public UserDto convertToUserDto(User user) {
    final UserDto userDto = modelMapper.map(user, UserDto.class);
    if (user.getProfile() != null) {
      userDto.setProfile(profileDtoMapper.convertToDto(user.getProfile()));
    }
    if (!user.getFamilies().isEmpty()) {
      userDto.setFamilies(new HashSet<>());
      for (final UserFamily userFamily : user.getFamilies()) {
        userDto.addFamily(familyDtoMapper.convertToDto(userFamily.getTheme()));
      }
    }
    return userDto;
  }
}
