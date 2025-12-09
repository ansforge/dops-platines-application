/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.service;

import java.io.File;

/** The File service. */
public interface FileService {
  /**
   * Gets the session zip file by uuid.
   *
   * @param uuidSession uuid of the session
   * @return zip file
   */
  File getSessionZipByFilename(String uuidSession);
}
