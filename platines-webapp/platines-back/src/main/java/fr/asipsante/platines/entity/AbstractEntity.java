/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.entity;

import java.io.Serializable;
import javax.annotation.Nullable;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import org.springframework.security.acls.model.Sid;

/**
 * Abstract entity for the model.
 *
 * @author apierre
 */
@MappedSuperclass
public abstract class AbstractEntity implements Serializable {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 5505597680901976444L;

  /** the id. */
  @Id @GeneratedValue private Long id;

  /**
   * @return the entity id
   */
  public Long getId() {
    return id;
  }

  /**
   * @param id the entity id to set
   */
  public void setId(Long id) {
    this.id = id;
  }

  @Nullable
  public abstract Sid getOwner();
}
