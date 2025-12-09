/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.entity;

import fr.asipsante.platines.entity.listeners.VisibilityListener;
import java.io.Serializable;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * Class Family corresponding to the table "famille" in the database. In the application Family
 * matches "thème".
 *
 * @author apierre
 */
@Entity
@Table(name = "famille")
@DiscriminatorValue("1")
@EntityListeners(VisibilityListener.class)
public class Theme extends Association implements Serializable {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 8636975489406700933L;

  /** The family name. */
  @Column(name = "nom", length = 50, unique = true)
  private String name;

  /** The family visibility. */
  @Column(name = "visibilite")
  private boolean visibility;

  /** Theme users. */
  @OneToMany(
      mappedBy = "theme",
      cascade = {CascadeType.REMOVE},
      orphanRemoval = true)
  private Set<UserFamily> users;

  /** Constructors. */
  public Theme() {
    super();
  }

  /**
   * Instantiates a new Family.
   *
   * @param name the name
   * @param visibility the visibility
   * @param users the users
   */
  public Theme(String name, boolean visibility, Set<UserFamily> users) {
    super();
    this.name = name;
    this.visibility = visibility;
    this.users = users;
  }

  /**
   * Adds family user.
   *
   * @param user the user
   */
  public void addUser(User user) {

    final UserFamily userFamily = new UserFamily(user, this);
    users.add(userFamily);
    user.getFamilies().add(userFamily);
  }

  /**
   * Removes family user.
   *
   * @param user the user
   */
  public void removeUser(User user) {

    final UserFamily userFamily = new UserFamily(user, this);
    users.remove(userFamily);
    user.getFamilies().remove(userFamily);
  }

  /**
   * Gets the family users.
   *
   * @return the family users.
   */
  public Set<UserFamily> getUsers() {
    return users;
  }

  /**
   * Sets the family users.
   *
   * @param users the users
   */
  public void setUsers(Set<UserFamily> users) {
    this.users = users;
  }

  /**
   * Gets the family name.
   *
   * @return the family name.
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the family name.
   *
   * @param name the name
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Gets the family visibility.
   *
   * @return true if the user can see it, false else.
   */
  public boolean getVisibility() {
    return visibility;
  }

  /**
   * Sets the family visibility.
   *
   * @param visibility the visibility
   */
  public void setVisibility(boolean visibility) {
    this.visibility = visibility;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    final Theme other = (Theme) obj;
    if (name == null) {
      if (other.name != null) return false;
    } else if (!name.equals(other.name)) return false;
    return true;
  }
}
