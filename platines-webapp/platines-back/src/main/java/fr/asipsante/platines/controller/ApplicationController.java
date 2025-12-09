/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.controller;

import fr.asipsante.platines.dto.ApplicationDto;
import fr.asipsante.platines.dto.ApplicationListDto;
import fr.asipsante.platines.dto.UserDto;
import fr.asipsante.platines.service.ApplicationService;
import fr.asipsante.platines.service.UserService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author apierre
 */
@RequestMapping("/secure/")
@RestController
public class ApplicationController {

  /** The id for the user profile in database. */
  private static final Long USER_ID = 2L;

  /** Logger */
  private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationController.class);

  /** The application service. */
  @Autowired private ApplicationService applicationService;

  /** The user service. */
  @Autowired private UserService userService;

  /**
   * Gets all the applications.
   *
   * @param token the connection token
   * @return all the applications
   */
  @GetMapping(value = "application/getAll", produces = "application/json")
  public List<ApplicationListDto> getAll(@RequestHeader(value = "Authorization") String token) {
    List<ApplicationListDto> applicationListDtos;
    final UserDto userDto = userService.getUserByToken(token);
    if (userDto.getProfile().getId().equals(USER_ID)) {
      applicationListDtos = applicationService.getApplicationsByUser(userDto.getId());
    } else {
      applicationListDtos = applicationService.getApplications();
    }

    return applicationListDtos;
  }

  /**
   * Gets a application by is id.
   *
   * @param id the application id
   * @return the application
   */
  @GetMapping(value = "application/get/{id}", produces = "application/json")
  public ApplicationDto getApplicationById(@PathVariable Long id) {
    return applicationService.getById(id);
  }

  /**
   * Persists a new application in the database.
   *
   * @param applicationDto the new application to persist
   */
  @PostMapping(value = "application/create", produces = "application/json")
  public void createApplication(@RequestBody ApplicationDto applicationDto) {
    applicationService.createApplication(applicationDto);
  }

  /**
   * Updates an application.
   *
   * @param applicationDto the application with the updates
   */
  @PostMapping(value = "application/update", produces = "application/json")
  public void updateApplication(@RequestBody ApplicationDto applicationDto) {
    applicationService.updateApplication(applicationDto);
  }

  /**
   * Deletes an application from the database.
   *
   * @param id id of the application to delete
   */
  @DeleteMapping(value = "application/delete/{id}", produces = "application/json")
  public void deleteApplication(@PathVariable Long id) {
    applicationService.deleteApplication(id);
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<Object> handleDataIntegrityViolation(DataIntegrityViolationException e) {
    LOGGER.debug("Data integrity violation: ", e);
    return ResponseEntity.badRequest().build();
  }
}
