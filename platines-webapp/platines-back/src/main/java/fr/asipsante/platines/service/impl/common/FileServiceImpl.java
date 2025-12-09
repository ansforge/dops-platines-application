/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.service.impl.common;

import fr.asipsante.platines.service.FileService;
import java.io.File;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service(value = "fileService")
public class FileServiceImpl implements FileService {

  @Value("${workspace}")
  private String workspace;

  @Override
  public File getSessionZipByFilename(String fileName) {
    String pathToFile = workspace + File.separator + "ready" + File.separator + fileName;
    File zip = new File(pathToFile);
    if (zip.exists()) {
      return zip;
    } else {
      return null;
    }
  }
}
