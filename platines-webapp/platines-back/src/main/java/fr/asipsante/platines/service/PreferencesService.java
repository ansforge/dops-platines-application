/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.service;

import java.util.Map;
import java.util.Properties;

/**
 * Preference service.
 *
 * @author aboittiaux
 */
public interface PreferencesService {

  /**
   * Récupération des préférences de l'application.
   *
   * @return properties preferences
   */
  Properties getPreferences();

  /**
   * Mise à jour de la propriété voulue.
   *
   * @param preference preference
   */
  void updatePreferences(Map<String, Object> preference);

  /**
   * Ajout de la propriété voulue.
   *
   * @param preference preference
   */
  void addPreference(Map<String, Object> preference);
}
