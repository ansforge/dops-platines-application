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
 * Class Profile corresponding to the table "profile" in the database.
 *
 * @author apierre
 */
@Entity
@Table(name = "profile")
public class Profile extends AbstractEntity implements Serializable {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -3462127302340620543L;

  /** Profile label. */
  @Column(name = "libelle", length = 20)
  private String label;

  /**
   * Gets the profile label.
   *
   * @return the profile label.
   */
  public String getLabel() {
    return label;
  }

  /**
   * Sets the profile label.
   *
   * @param label the label
   */
  public void setLabel(String label) {
    this.label = label;
  }

  @Override
  public Sid getOwner() {
    return null;
  }
}
