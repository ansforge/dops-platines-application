/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.dto;

/**
 * @author apierre
 */
public class SessionDurationDto {

  /** The session duration id. */
  private Long id;

  /** The session duration. */
  private Long duration;

  /**
   * Gets the session duration id.
   *
   * @return the id
   */
  public Long getId() {
    return id;
  }

  /**
   * Sets the session duration id.
   *
   * @param id, the new id to set
   */
  public void setId(Long id) {
    this.id = id;
  }

  /**
   * Gets the session duration.
   *
   * @return the duration
   */
  public Long getDuration() {
    return duration;
  }

  /**
   * Sets the session duration.
   *
   * @param duration, the new duration to set
   */
  public void setDuration(Long duration) {
    this.duration = duration;
  }
}
