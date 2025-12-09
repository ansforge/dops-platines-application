/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.dto;

/**
 * @author apierre
 */
public class VersionDto {

  /** version id. */
  private Long id;

  /** version label. */
  private String label;

  /** version description. */
  private String description;

  /** version visibility. */
  private boolean visibility;

  /** version service. */
  private SimulatedServiceDto service;

  /** copnstructeur. */
  public VersionDto() {
    super();
  }

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

  /**
   * @return the description
   */
  public String getDescription() {
    return description;
  }

  /**
   * @param description the description to set
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * @return the visibility
   */
  public boolean isVisibility() {
    return visibility;
  }

  /**
   * @param visibility the visibility to set
   */
  public void setVisibility(boolean visibility) {
    this.visibility = visibility;
  }

  /**
   * @return the service
   */
  public SimulatedServiceDto getService() {
    return service;
  }

  /**
   * @param service the service to set
   */
  public void setService(SimulatedServiceDto service) {
    this.service = service;
  }
}
