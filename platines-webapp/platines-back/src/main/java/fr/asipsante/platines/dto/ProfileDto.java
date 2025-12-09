/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author apierre
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProfileDto {

  /** Profile id. */
  private Long id;

  /** Profile label. */
  private String label;

  /**
   * @return the id
   */
  public Long getId() {
    return id;
  }

  /**
   * @param id the id to set
   */
  public void setId(Long id) {
    this.id = id;
  }

  /**
   * @return the label
   */
  public String getLabel() {
    return label;
  }

  /**
   * @param label the label to set
   */
  public void setLabel(String label) {
    this.label = label;
  }
}
