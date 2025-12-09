/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.ans.platines.model;

import java.io.File;
import java.util.Map;
import java.util.Properties;

/**
 * @author aboittiaux
 */
public class DriverContext {

  /** Map d'options du driver */
  private Map<String, String> options;

  /** Properties du test. */
  private Properties properties;

  /** Répertoire où se trouver le fichier de test. */
  private File projectDirectory;

  /**
   * @return the properties
   */
  public Properties getProperties() {
    return properties;
  }

  /**
   * @param properties the properties to set
   */
  public void setProperties(Properties properties) {
    this.properties = properties;
  }

  /**
   * @return the projectDirectory
   */
  public File getProjectDirectory() {
    return projectDirectory;
  }

  /**
   * @param projectDirectory the projectDirectory to set
   */
  public void setProjectDirectory(File projectDirectory) {
    this.projectDirectory = projectDirectory;
  }

  public Map<String, String> getOptions() {
    return options;
  }

  public void setOptions(Map<String, String> options) {
    this.options = options;
  }
}
