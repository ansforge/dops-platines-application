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
public class SimulatedServiceTreeDto {

  /** The service id. */
  private Long id;

  /** The service name. */
  private String name;

  /** Children versions */
  @JsonProperty("children")
  private VersionDto[] versions;
}
