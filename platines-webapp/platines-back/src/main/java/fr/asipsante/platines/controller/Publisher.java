/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.controller;

import com.google.gson.Gson;
import fr.asipsante.platines.dto.ServerOperation;
import fr.asipsante.platines.model.DriverSessionResult;
import fr.asipsante.platines.model.DriverTestResult;
import fr.asipsante.platines.service.SessionService;
import fr.asipsante.platines.service.UserService;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author apierre
 */
@RequestMapping("/insecure/")
@RestController
public class Publisher {

  /** The session service. */
  @Autowired SessionService sessionService;

  /** The user service. */
  @Autowired UserService userService;

  /**
   * Update the session.
   *
   * @param result the result
   */
  @PostMapping(value = "publisher/updateSession", produces = "application/json")
  public void updateSession(@RequestParam String result) {
    final Gson gson = new Gson();
    final DriverSessionResult driverSessionResult =
        gson.fromJson(result, DriverSessionResult.class);
    sessionService.updateStatusSession(driverSessionResult);
  }

  /**
   * Adds log to the session.
   *
   * @param uuid the session uuid
   * @param file the log file
   * @throws IOException IO Exception
   */
  @PostMapping(value = "publisher/addLogSession", produces = "application/json")
  public void addLogSession(@RequestParam String uuid, @RequestParam MultipartFile file)
      throws IOException {
    sessionService.uploadLogSession(uuid, file.getBytes());
  }

  /**
   * Saves the result project.
   *
   * @param driverProjectResult the driver
   */
  @PostMapping(value = "publisher/saveResultRProjet", produces = "application/json")
  public void saveResultRProjet(@RequestParam String driverProjectResult) {
    final Gson gson = new Gson();
    final DriverTestResult soapUIDriverProjectResult =
        gson.fromJson(driverProjectResult, DriverTestResult.class);
    sessionService.updateSessionProject(soapUIDriverProjectResult);
  }

  /**
   * Saves the server operation.
   *
   * @param operation the server operation
   */
  @PostMapping(value = "publisher/saveServerOperation", produces = "application/json")
  public void saveServerOperation(@RequestParam String operation) {
    final Gson gson = new Gson();
    final ServerOperation serverOperation = gson.fromJson(operation, ServerOperation.class);
    sessionService.saveSessionServerHistory(serverOperation);
  }
}
