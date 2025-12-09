/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.service;

import fr.asipsante.platines.entity.Nomenclature;
import java.util.Date;
import org.springframework.web.multipart.MultipartFile;

/**
 * Nomanclature Service.
 *
 * @author apierre
 */
public interface NomenclatureService {

  /**
   * Persists a new nomenclature in the database.
   *
   * @param file, the nomenclature to persist
   * @param fileName, the file name
   */
  void saveNomenclature(MultipartFile file, String fileName);

  /**
   * Gets the date of the last nomenclature update.
   *
   * @return the last update date
   */
  Date getLastUpdateNomenclature();

  /**
   * Gets the last nomenclature register in database.
   *
   * @return the last nomenclature
   */
  Nomenclature getLastNomenclature();
}
