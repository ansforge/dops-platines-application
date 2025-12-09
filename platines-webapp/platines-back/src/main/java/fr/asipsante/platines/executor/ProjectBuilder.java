/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.executor;

import fr.asipsante.platines.executor.model.ProjectDetail;
import java.util.List;

/**
 * @author aboittiaux
 */
public interface ProjectBuilder {

  /**
   * Gets the details of a project.
   *
   * @param projectFile, the project file content
   * @return the project details
   */
  ProjectDetail getProjectDetail(byte[] projectContent);

  List<String> getMockContextPath(byte[] projectFile);
}
