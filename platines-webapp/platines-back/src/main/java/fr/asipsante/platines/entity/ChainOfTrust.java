/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.entity;

import fr.asipsante.platines.entity.listeners.GlobalListener;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.springframework.security.acls.model.Sid;

/**
 * Class ChainOfTrust corresponding to the table "chaine_de_confiance" in the database.
 *
 * @author apierre
 */
@Entity
@Table(name = "chaine_de_confiance")
@EntityListeners(GlobalListener.class)
public class ChainOfTrust extends AbstractEntity implements Serializable {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 0L;

  /** Chain of trust name. */
  @Column(name = "nom", length = 50)
  private String name;

  /** Chain of trust description. */
  @Column(name = "description", length = 500)
  private String description;

  /** Chain of trust owner. */
  @ManyToOne
  @JoinColumn(name = "USER_ID", nullable = false)
  private User user;

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * @param name the name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Gets the chain of trust description.
   *
   * @return the chain of trust description
   */
  public String getDescription() {
    return description;
  }

  /**
   * Sets the chain of trust description.
   *
   * @param description, the new chain of trust description to set
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * @return the user
   */
  public User getUser() {
    return user;
  }

  /**
   * @param user the user to set
   */
  public void setUser(User user) {
    this.user = user;
  }

  @Override
  public Sid getOwner() {
    return null;
  }
}
