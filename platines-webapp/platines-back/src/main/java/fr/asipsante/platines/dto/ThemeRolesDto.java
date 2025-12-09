/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.dto;

import fr.asipsante.platines.entity.enums.Role;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Information on family role coverage.
 *
 * @author edegenetais
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ThemeRolesDto {
  private ThemeDto family;
  private Set<Role> coveredRoles;
}
