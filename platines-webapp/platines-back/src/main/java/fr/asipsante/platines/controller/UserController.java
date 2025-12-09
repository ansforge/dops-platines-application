/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.controller;

import fr.asipsante.platines.dto.UserDto;
import fr.asipsante.platines.dto.UserProfile;
import fr.asipsante.platines.service.UserService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author apierre
 */
@RequestMapping("/secure/")
@RestController
public class UserController {

  /** The user service. */
  @Autowired private UserService userService;

  /**
   * Gets all the users.
   *
   * @return the list of the users
   */
  @PreAuthorize("hasRole('admin') or hasRole('manager')")
  @GetMapping(value = "user/getAll", produces = "application/json")
  public List<UserDto> getAll() {
    return userService.getAllUsers();
  }

  /**
   * Gets all unassigned users, for anyone who asks.
   *
   * @return the list of the users
   */
  @PreAuthorize("hasRole('admin') or hasRole('manager')")
  @GetMapping(value = "user/safeFetchAllUnassigned", produces = "application/json")
  public List<UserDto> safeFetchAllUnassigned() {
    return userService.safeFetchAllUnassignedUsers();
  }

  /**
   * Persists a new user in database.
   *
   * @param userDto, the user to persist
   */
  @PreAuthorize("hasRole('admin')")
  @PostMapping(value = "user/add", produces = "application/json")
  public void add(@RequestBody UserDto userDto) {
    userService.createUser(userDto);
  }

  /**
   * Gets a user by is id.
   *
   * @param userId, id of the user to find
   * @return the user
   */
  @PreAuthorize("hasRole('admin') or hasRole('manager')")
  @GetMapping(value = "user/get/{userId}", produces = "application/json")
  public UserDto getUserById(@PathVariable Long userId) {
    return userService.getUserById(userId);
  }

  /**
   * Gets a user by its mail.
   *
   * @param mail, the user mail
   * @return the user
   */
  @PostMapping(value = "user/getMail/", produces = "application/json")
  public UserDto getUserById(@RequestBody String mail) {
    return userService.getUserByMail(mail);
  }

  /**
   * Updates a user.
   *
   * @param userDto, user to update
   */
  @PreAuthorize("hasRole('admin')")
  @PostMapping(value = "user/update", produces = "application/json")
  public void update(@RequestBody UserDto userDto) {
    userService.updateUser(userDto);
  }

  /**
   * Updates the user families.
   *
   * @param userDto, user to update
   */
  @PreAuthorize("hasRole('admin') or hasRole('manager')")
  @PostMapping(value = "user/updateFamilies", produces = "application/json")
  public void updateFamily(@RequestBody UserDto userDto) {
    userService.updateUserFamilies(userDto);
  }

  /**
   * Deletes a user from the database.
   *
   * @param userId, id of the user to delete
   */
  @PreAuthorize("hasRole('admin')")
  @DeleteMapping(value = "user/delete/{userId}", produces = "application/json")
  public void deleteUser(@PathVariable Long userId) {
    userService.deleteUser(userId);
  }

  /**
   * Updates a user.
   *
   * @param user, the user with updates
   */
  @PostMapping(value = "/user/updateProfile", produces = "application/json")
  public void updateProfile(@RequestBody UserProfile user) {
    userService.updateProfile(user);
  }
}
