/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import org.springframework.security.acls.model.Sid;

/**
 * Class SessionDuration corresponding to the table "duree_session" in the database.
 *
 * @author apierre
 */
@Entity
@Table(name = "duree_session")
public class SessionDuration extends AbstractEntity implements Serializable {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;

  /** The session duration. */
  @Column(name = "duree")
  private Long duration;

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

  @Override
  public Sid getOwner() {
    return null;
  }
}
