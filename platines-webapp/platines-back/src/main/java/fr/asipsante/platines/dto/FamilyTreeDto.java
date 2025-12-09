/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FamilyTreeDto {

  /** The family id. */
  private Long id;

  /** The family name. */
  private String name;

  /** The family visibility. */
  private boolean visibility;

  /** Children web services */
  @JsonProperty("children")
  private SimulatedServiceTreeDto[] systems;
}
