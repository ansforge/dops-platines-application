/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.controller;

import fr.asipsante.platines.service.FileService;
import java.io.File;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/insecure/")
@RestController
public class DownloadController {

  @Autowired
  @Qualifier("fileService")
  private FileService fileService;

  @GetMapping("download/{fileName}")
  @ResponseBody
  public ResponseEntity<FileSystemResource> downloadZip(@PathVariable String fileName) {
    File projectFile = fileService.getSessionZipByFilename(fileName);
    if (projectFile != null) {
      FileSystemResource resource = new FileSystemResource(projectFile);
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.add(
          HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + projectFile.getName());
      responseHeaders.add(HttpHeaders.CONTENT_TYPE, "application/zip");
      responseHeaders.add(HttpHeaders.CONTENT_LENGTH, String.valueOf(projectFile.length()));
      return new ResponseEntity<>(resource, responseHeaders, HttpStatus.OK);
    } else {
      return new ResponseEntity<>(null, null, HttpStatus.NOT_FOUND);
    }
  }
}
