/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * Class UserFamily corresponding to the table "user_famille" in the database.
 *
 * @author cnader
 */
@Entity
@Table(name = "user_famille")
public class UserFamily implements Serializable {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 314536780793428144L;

  /** The user. */
  @Id
  @NotNull
  @ManyToOne
  @JoinColumn(name = "User_ID")
  private User user;

  /** user theme. */
  @Id
  @NotNull
  @ManyToOne
  @JoinColumn(name = "FAMILY_ID")
  private Theme theme;

  /** Constructors. */
  public UserFamily() {}

  public UserFamily(User user, Theme theme) {
    this.user = user;
    this.theme = theme;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public Theme getTheme() {
    return theme;
  }

  public void setTheme(Theme theme) {
    this.theme = theme;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((theme == null) ? 0 : theme.hashCode());
    result = prime * result + ((user == null) ? 0 : user.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    final UserFamily other = (UserFamily) obj;
    if (theme == null) {
      if (other.theme != null) return false;
    } else if (!theme.equals(other.theme)) return false;
    if (user == null) {
      if (other.user != null) return false;
    } else if (!user.equals(other.user)) return false;
    return true;
  }
}
