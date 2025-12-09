/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.service;

import fr.asipsante.platines.dto.UserDto;
import fr.asipsante.platines.dto.UserProfile;
import java.util.List;

/**
 * The user service.
 *
 * @author apierre
 */
public interface UserService {

  /**
   * Gets all the users.
   *
   * @return the users list
   */
  List<UserDto> getAllUsers();

  /**
   * Creates a user.
   *
   * @param userDto the user dto
   */
  void createUser(UserDto userDto);

  /**
   * Updates a user.
   *
   * @param userDto the user dto
   */
  void updateUser(UserDto userDto);

  /**
   * Deletes a user.
   *
   * @param userId the user id
   */
  void deleteUser(Long userId);

  /**
   * Gets a user by its token.
   *
   * @param token the token
   * @return a user
   */
  UserDto getUserByToken(String token);

  /**
   * Gets a user by its id.
   *
   * @param idUser the id user
   * @return the user
   */
  UserDto getUserById(Long idUser);

  /**
   * Updates the user profile.
   *
   * @param user the user
   */
  void updateProfile(UserProfile user);

  /**
   * Gets a user by its mail address.
   *
   * @param mail the mail
   * @return the user
   */
  UserDto getUserByMail(String mail);

  /**
   * Updates the families of a user.
   *
   * @param userDto the user dto
   */
  void updateUserFamilies(UserDto userDto);

  /**
   * Gets all unassigned users, for anyone who asks.
   *
   * @return the users list
   */
  List<UserDto> safeFetchAllUnassignedUsers();
}
