/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.controller;

import fr.asipsante.platines.service.PreferencesService;
import java.util.Map;
import java.util.Properties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author aboittiaux
 */
@RequestMapping("/secure/")
@RestController
public class PreferencesController {

  /** The preference service. */
  @Autowired PreferencesService preferencesService;

  /**
   * Gets all the preferences.
   *
   * @return all the preferences
   */
  @PreAuthorize("hasRole('admin')")
  @GetMapping(value = "preferences", produces = "application/json")
  public Properties getAllPreferences() {
    return preferencesService.getPreferences();
  }

  /**
   * Updates the preferences.
   *
   * @param preferences the preferences to update
   */
  @PreAuthorize("hasRole('admin')")
  @PostMapping(value = "preferences/update")
  public void updatePreferences(@RequestBody Map<String, Object> preferences) {
    preferencesService.updatePreferences(preferences);
  }

  /**
   * Adds a preference.
   *
   * @param preferences the preferences list with the new preference
   */
  @PreAuthorize("hasRole('admin')")
  @PostMapping(value = "preferences/add")
  public void addPreferences(@RequestBody Map<String, Object> preferences) {
    preferencesService.updatePreferences(preferences);
  }
}
