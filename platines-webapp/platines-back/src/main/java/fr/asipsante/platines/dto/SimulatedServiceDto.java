/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.dto;

/**
 * @author apierre
 */
public class SimulatedServiceDto {

  /** The service id. */
  private Long id;

  /** The service name. */
  private String name;

  /** The service theme. */
  private ThemeDto theme;

  /** Default constructor. */
  public SimulatedServiceDto() {
    super();
  }

  /**
   * Gets the service id.
   *
   * @return the service id
   */
  public Long getId() {
    return id;
  }

  /**
   * Sets the service id.
   *
   * @param id, the service id to set
   */
  public void setId(Long id) {
    this.id = id;
  }

  /**
   * Gets the service name.
   *
   * @return the service name
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the service name.
   *
   * @param name, the name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Gets the service theme.
   *
   * @return the service theme
   */
  public ThemeDto getTheme() {
    return theme;
  }

  /**
   * Sets the service theme.
   *
   * @param theme, the service theme to set
   */
  public void setTheme(ThemeDto theme) {
    this.theme = theme;
  }
}
