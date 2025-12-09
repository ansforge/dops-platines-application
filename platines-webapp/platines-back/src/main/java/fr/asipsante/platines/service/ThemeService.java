/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.service;

import fr.asipsante.platines.dto.FamilyTreeDto;
import fr.asipsante.platines.dto.ThemeDto;
import fr.asipsante.platines.dto.ThemeRolesDto;
import java.util.List;

public interface ThemeService {

  /**
   * Get all the families register in the database.
   *
   * @return all the families DTO
   */
  List<ThemeDto> getAllFamilies();

  /**
   * List all the families registered in the database using the tree-friendly dtos
   *
   * @return all the families as DTO with child web services with child versions
   */
  List<FamilyTreeDto> getAllFamiliesAsTree();

  /**
   * Converts a familyDto to a Family Entity and adds it in the database.
   *
   * @param familyDto the family dto
   */
  void createFamily(ThemeDto familyDto);

  /**
   * Gets a family by is id.
   *
   * @param id the id
   * @return the family corresponding
   */
  ThemeDto getFamilyById(Long id);

  /**
   * Updates a family save in database.
   *
   * @param familyDto the family dto
   */
  void updateFamily(ThemeDto familyDto);

  /**
   * Deletes a family in the database.
   *
   * @param id the id
   */
  void deleteFamily(Long id);

  /**
   * Fetches families with role coverage data.
   *
   * @return
   */
  List<ThemeRolesDto> getThemeRoleCoverage();
}
