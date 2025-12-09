/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.model;

import java.io.File;
import java.util.Map;

/**
 * @author aboittiaux
 */
public class DriverContext {

  /** Map d'options du driver */
  private Map<String, String> options;

  /** Répertoire où se trouver le fichier de test. */
  private File projectDirectory;

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
