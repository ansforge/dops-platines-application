/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.controller;

import fr.asipsante.platines.dto.UserDto;
import fr.asipsante.platines.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author aboittiaux
 */
@RequestMapping("/secure/")
@RestController
public class ProfileController {

  /** The user service. */
  @Autowired private UserService userService;

  /**
   * Updates the user profile.
   *
   * @param userDto user to update
   */
  @PostMapping(value = "editProfile", produces = "application/json")
  public void updateProfile(UserDto userDto) {
    userService.updateUser(userDto);
  }
}
