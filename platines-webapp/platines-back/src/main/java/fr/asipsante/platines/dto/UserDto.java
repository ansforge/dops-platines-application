/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.dto;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * The type User dto.
 *
 * @author apierre
 */
@Getter
@Setter
@AllArgsConstructor
public class UserDto {

  /** The user id. */
  private Long id;

  /** User mail. */
  private String mail;

  /** User name. */
  private String name;

  /** User forename. */
  private String forename;

  /** User firm. */
  private String firm;

  /** User creation date. */
  private Date creationDate;

  /** User profile. */
  private ProfileDto profile;

  /** User theme. */
  private Set<ThemeDto> families = new HashSet<>();

  /** Constructeur. */
  public UserDto() {
    super();
  }

  /**
   * Adds user family.
   *
   * @param familyDto the family dto
   */
  public void addFamily(ThemeDto familyDto) {
    families.add(familyDto);
    familyDto.getUsers().add(this);
  }
}
