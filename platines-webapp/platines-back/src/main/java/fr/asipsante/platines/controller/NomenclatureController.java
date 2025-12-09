/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.controller;

import fr.asipsante.platines.service.NomenclatureService;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author apierre
 */
@RequestMapping("/secure")
@RestController
public class NomenclatureController {

  /** The nomenclature service. */
  @Autowired
  @Qualifier("NomenclatureService")
  private NomenclatureService nomenclatureService;

  /**
   * Save a nomenclature zipfile in the database.
   *
   * @param zipFile the nomenclature zip
   * @param fileName the file name
   */
  @PreAuthorize("hasRole('admin') or hasRole('manager')")
  @PostMapping(value = "/nomenclature/create", headers = "Content-Type= multipart/form-data")
  public void saveNomenclature(@RequestParam MultipartFile zipFile, @RequestParam String fileName) {
    nomenclatureService.saveNomenclature(zipFile, fileName);
  }

  /**
   * Gets the last date upload.
   *
   * @return the last date upload
   */
  @PreAuthorize("hasRole('admin') or hasRole('manager')")
  @GetMapping(value = "/nomenclature/last", produces = "application/json")
  public Date getLastUpdateNomenclature() {
    return nomenclatureService.getLastUpdateNomenclature();
  }
}
