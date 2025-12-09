/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.controller;

import fr.asipsante.platines.dto.FunctionalDataDto;
import fr.asipsante.platines.service.HomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author apierre
 */
@RequestMapping("/secure")
@RestController
public class HomeController {

  /** The home service. */
  @Autowired
  @Qualifier("homeService")
  private HomeService homeService;

  /**
   * Changes the text from the home page.
   *
   * @param content the new text to persist in database
   * @return the functional data to display
   */
  @PreAuthorize("hasRole('admin')")
  @PostMapping(value = "/functionalData/update", produces = "application/json")
  public FunctionalDataDto changeTextHomePage(@RequestBody String content) {
    return homeService.updateProperties(content);
  }

  /**
   * Gets the functional data to display on home page.
   *
   * @return the functional data
   */
  @GetMapping(value = "/functionalData/homepage", produces = "application/json")
  public FunctionalDataDto getContentHomePage() {
    return homeService.getFunctionalData();
  }
}
