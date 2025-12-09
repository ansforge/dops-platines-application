/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author apierre
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ThemeDto {

  /** The family id. */
  private Long id;

  /** The family name. */
  private String name;

  /** The family visibility. */
  private boolean visibility;

  /** The family users. */
  @JsonIgnore private Set<UserDto> users = new HashSet<>();
}
